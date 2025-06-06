import Foundation
import HealthKit
import CoreMotion
import WatchKit

class SleepManager: ObservableObject {
    private let healthStore = HKHealthStore()
    private let motionManager = CMMotionManager()
    private var workoutSession: HKWorkoutSession?
    private var builder: HKLiveWorkoutBuilder?
    private var timer: Timer?
    private var heartRateQuery: HKAnchoredObjectQuery?

    private var accFileHandle: FileHandle?
    private var hrFileHandle: FileHandle?

    private var currentMeasurementFolder: URL {
        let dateStr = ISO8601DateFormatter().string(from: Date())
        return FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
            .appendingPathComponent("measurement_\(dateStr)")
    }

    private var accFileURL: URL {
        currentMeasurementFolder.appendingPathComponent("accelerometer_data.txt")
    }

    private var hrFileURL: URL {
        currentMeasurementFolder.appendingPathComponent("heart_rate_data.txt")
    }

    @Published var isMeasuring = false
    @Published var elapsedTime: TimeInterval = 0
    var startTime: Date?

    var elapsedTimeString: String {
        let t = Int(elapsedTime)
        let h = t / 3600
        let m = (t % 3600) / 60
        let s = t % 60
        return String(format: "%02d:%02d:%02d", h, m, s)
    }

    func startMeasurement() {
        isMeasuring = true
        startTime = Date()
        elapsedTime = 0
        requestAuthorization()
        startWorkoutSession()
        startMotionUpdates()
        startTimer()
        openFile()
    }

    func stopMeasurement() {
        isMeasuring = false
        timer?.invalidate()
        timer = nil
        motionManager.stopAccelerometerUpdates()

        if let query = heartRateQuery {
            healthStore.stop(query)
            heartRateQuery = nil
        }

        if let session = workoutSession {
            session.end()
            workoutSession = nil
        }

        builder?.endCollection(withEnd: Date()) { [weak self] _, _ in
            self?.builder?.finishWorkout(completion: { _, _ in })
            self?.builder = nil
        }

        accFileHandle?.closeFile()
        hrFileHandle?.closeFile()
    }

    private func startTimer() {
        timer = Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { [weak self] _ in
            guard let self = self, let start = self.startTime else { return }
            self.elapsedTime = Date().timeIntervalSince(start)
        }
    }

    private func startMotionUpdates() {
        motionManager.accelerometerUpdateInterval = 0.1
        motionManager.startAccelerometerUpdates(to: .main) { [weak self] data, _ in
            guard let self = self, let data = data else { return }
            self.appendAccelerometer(timestamp: Date().timeIntervalSince1970, acc: data.acceleration)
        }
    }

    private func openFile() {
        do {
            try FileManager.default.createDirectory(at: currentMeasurementFolder, withIntermediateDirectories: true)
        } catch {
            print("❌ Failed to create measurement folder: \(error)")
        }

        FileManager.default.createFile(atPath: accFileURL.path, contents: nil)
        FileManager.default.createFile(atPath: hrFileURL.path, contents: nil)

        do {
            accFileHandle = try FileHandle(forWritingTo: accFileURL)
            hrFileHandle = try FileHandle(forWritingTo: hrFileURL)
            accFileHandle?.seekToEndOfFile()
            hrFileHandle?.seekToEndOfFile()
        } catch {
            print("❌ Failed to open file handles: \(error)")
        }
    }

    private func appendAccelerometer(timestamp: TimeInterval, acc: CMAcceleration) {
        guard isMeasuring else { return }
        guard let handle = accFileHandle else { return }

        let line = String(format: "%.0f,%.4f,%.4f,%.4f\n", timestamp, acc.x, acc.y, acc.z)
        if let data = line.data(using: .utf8) {
            try? handle.write(contentsOf: data)
        }
    }

    private func appendHeartRate(timestamp: TimeInterval, bpm: Double) {
        guard isMeasuring else {
            print("⏹️ Ignoring heart rate write — not measuring")
            return
        }

        guard let handle = hrFileHandle else {
            print("❌ Tried to write to HR file but handle was nil")
            return
        }

        let line = String(format: "%.0f,%.1f\n", timestamp, bpm)
        if let data = line.data(using: .utf8) {
            do {
                try handle.write(contentsOf: data)
            } catch {
                print("❌ Write failed: \(error)")
            }
        }
    }

    private func requestAuthorization() {
        let types: Set = [HKQuantityType.quantityType(forIdentifier: .heartRate)!]
        healthStore.requestAuthorization(toShare: nil, read: types) { _, _ in }
    }

    private var latestHeartRate: Double?

    private func startWorkoutSession() {
        guard HKHealthStore.isHealthDataAvailable() else { return }

        let config = HKWorkoutConfiguration()
        config.activityType = .other
        config.locationType = .unknown

        do {
            workoutSession = try HKWorkoutSession(healthStore: healthStore, configuration: config)
            builder = workoutSession?.associatedWorkoutBuilder()
            builder?.dataSource = HKLiveWorkoutDataSource(healthStore: healthStore, workoutConfiguration: config)

            workoutSession?.startActivity(with: Date())
            builder?.beginCollection(withStart: Date()) { _, _ in }

            startHeartRateQuery()
        } catch {
            print("Błąd uruchamiania sesji: \(error.localizedDescription)")
        }
    }

    private func startHeartRateQuery() {
        guard let type = HKObjectType.quantityType(forIdentifier: .heartRate) else { return }

        let predicate = HKQuery.predicateForSamples(withStart: Date(), end: nil, options: .strictStartDate)

        let query = HKAnchoredObjectQuery(type: type, predicate: predicate, anchor: nil, limit: HKObjectQueryNoLimit) {
            [weak self] (_, samplesOrNil, _, _, _) in
            self?.handleHeartRate(samples: samplesOrNil)
        }

        query.updateHandler = { [weak self] (_, samplesOrNil, _, _, _) in
            self?.handleHeartRate(samples: samplesOrNil)
        }

        self.heartRateQuery = query
        healthStore.execute(query)
    }

    private func handleHeartRate(samples: [HKSample]?) {
        guard let hrSamples = samples as? [HKQuantitySample] else { return }
        guard let last = hrSamples.last else { return }

        let bpm = last.quantity.doubleValue(for: .init(from: "count/min"))
        let timestamp = last.startDate.timeIntervalSince1970

        DispatchQueue.main.async {
            self.latestHeartRate = bpm
            self.appendHeartRate(timestamp: timestamp, bpm: bpm)
        }
    }
}
