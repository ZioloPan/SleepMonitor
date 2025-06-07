# Sleep Monitor

## WatchApp:

The `WatchApp` is a watchOS application designed for Apple Watch that monitors sleep by continuously recording sensor data:

- **Heart rate**
- **Accelerometer (movement)**

Measurements can run for several hours and are manually controlled by the user (Start/Stop). Collected data is saved locally as plain `.txt` files in separate folders per recording. Each measurement can be imported into the backend for further processing.

### Features

- Records heart rate and accelerometer data every second
- Stores data locally in `.txt` format
- Displays measurement history with import capability
- Timer-controlled measurement with real-time view

### Installation and Running Instructions

1. Open the `WatchApp` project in Xcode
2. Connect a physical Apple Watch or use the watchOS simulator
3. Build and run the app on the device
4. After completing a measurement, open the history tab and tap **Import** to send the data to the backend

## Backend:

The SleepMonitor backend handles the processing and management of sleep-related data. It integrates with Apple Watch data (heart rate and accelerometer) and exposes a REST API for data collection, analysis, and visualization.

### Installation and Running Instructions

1. Clone the Repository
2. Build database by running docker-compose.yml file
3. Start application by running BackendApplication file

Once the server is running, you can access the automatically generated API documentation at:
[API Documentation](http://localhost:8080/swagger-ui/index.html)



## ML-based Sleep Analysis Module:

This module classifies sleep stages (Wake, NREM, REM) using data from an accelerometer and a heart rate sensor. It performs synchronized preprocessing of raw motion and heart rate signals, extracts physiologically meaningful features (e.g., activity counts, heart rate variability, circadian rhythm), and uses a trained machine learning model to predict sleep stages on a second-by-second basis.

The module is based on methods adapted from ojwalch/sleep_classifiers and supports both batch processing and single-sequence prediction. The dataset used for training and testing is from PhysioNet Sleep Accel.

## Frontend:
