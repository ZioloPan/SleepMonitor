import SwiftUI

struct HistoryView: View {
    @State private var measurements: [URL] = []

    var body: some View {
        List {
            ForEach(measurements, id: \.self) { folder in
                VStack(alignment: .leading) {
                    Text(formattedDate(from: folder.lastPathComponent))
                        .font(.headline)

                    HStack {
                        Button("Importuj") {
                            importMeasurement(from: folder)
                        }
                        .tint(.blue)
                    }
                }
            }
            .onDelete(perform: deleteMeasurement)
        }
        .onAppear(perform: loadMeasurements)
    }

    private func loadMeasurements() {
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        if let contents = try? FileManager.default.contentsOfDirectory(at: docs, includingPropertiesForKeys: nil) {
            measurements = contents
                .filter { $0.lastPathComponent.hasPrefix("measurement_") }
                .sorted(by: { $0.lastPathComponent > $1.lastPathComponent })
        }
    }

    private func deleteMeasurement(at offsets: IndexSet) {
        for index in offsets {
            let folder = measurements[index]
            try? FileManager.default.removeItem(at: folder)
        }
        measurements.remove(atOffsets: offsets)
    }

    private func importMeasurement(from folder: URL) {
        let accURL = folder.appendingPathComponent("accelerometer_data.txt")
        let hrURL = folder.appendingPathComponent("heart_rate_data.txt")
        sendFile(accURL, to: "api/v1/acceleration/upload")
        sendFile(hrURL, to: "api/v1/heart_rate/upload")
    }

    private func sendFile(_ url: URL, to endpoint: String) {
        guard let requestURL = URL(string: "http://192.168.100.34:8080/\(endpoint)") else { return }

        let boundary = UUID().uuidString
        var request = URLRequest(url: requestURL)
        request.httpMethod = "POST"
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")

        let filename = url.lastPathComponent
        let mimeType = "text/plain"

        guard let fileData = try? Data(contentsOf: url) else {
            print("❌ Failed to read file \(url)")
            return
        }

        var body = Data()
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"\(filename)\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: \(mimeType)\r\n\r\n".data(using: .utf8)!)
        body.append(fileData)
        body.append("\r\n".data(using: .utf8)!)
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)

        URLSession.shared.uploadTask(with: request, from: body) { data, response, error in
            if let error = error {
                print("❌ Error uploading to \(endpoint): \(error)")
                return
            }

            if let httpResponse = response as? HTTPURLResponse {
                print("✅ Response: \(httpResponse.statusCode) from \(endpoint)")
            } else {
                print("✅ Uploaded to \(endpoint): \(filename)")
            }
        }.resume()
    }
    
    private func formattedDate(from folderName: String) -> String {
        let prefix = "measurement_"
        guard folderName.hasPrefix(prefix) else { return folderName }

        let isoDateString = String(folderName.dropFirst(prefix.count))
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withInternetDateTime]

        guard let date = formatter.date(from: isoDateString) else { return folderName }

        let displayFormatter = DateFormatter()
        displayFormatter.locale = Locale(identifier: "pl_PL")
        displayFormatter.dateStyle = .long
        displayFormatter.timeStyle = .medium

        return displayFormatter.string(from: date)
    }

}
