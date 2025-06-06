import SwiftUI

struct MeasurementView: View {
    @ObservedObject var sleepManager: SleepManager
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(spacing: 20) {
            if sleepManager.isMeasuring {
                Text("Czas pomiaru: \(sleepManager.elapsedTimeString)")
                    .monospacedDigit()

                Button("Zatrzymaj") {
                    sleepManager.stopMeasurement()
                    dismiss()
                }
                .tint(.red)
            } else {
                Button("Start") {
                    sleepManager.startMeasurement()
                }
                .tint(.green)
            }
        }
        .navigationTitle("Pomiar")
        .padding()
    }
}
