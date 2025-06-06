import SwiftUI

struct HomeView: View {
    @ObservedObject var sleepManager: SleepManager

    var body: some View {
        VStack(spacing: 20) {
            NavigationLink("Rozpocznij pomiar") {
                MeasurementView(sleepManager: sleepManager)
            }
            .tint(.green)

            NavigationLink("Historia pomiarów") {
                HistoryView()
            }
        }
        .padding()
    }
}
