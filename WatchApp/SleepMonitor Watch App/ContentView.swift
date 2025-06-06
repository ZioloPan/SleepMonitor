import SwiftUI

struct ContentView: View {
    @StateObject private var sleepManager = SleepManager()

    var body: some View {
        NavigationStack {
            HomeView(sleepManager: sleepManager)
        }
    }
}
