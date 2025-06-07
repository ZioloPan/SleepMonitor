# Dream Stage Recognition
The machine learning code was developed based on the project: https://github.com/ojwalch/sleep_classifiers
It uses data preprocessing and feature extraction methods from that repository.
Dataset: https://physionet.org/content/sleep-accel/1.0.0/
Scientific article: https://academic.oup.com/sleep/article/42/12/zsz180/5549536?login=false

# Description of the Data Processing and Machine Learning Module – Sleep Stage Classification Based on Accelerometer and Heart Rate Data
## 1. Program Objective
   The goal of this program is to classify sleep stages (wake, NREM, REM) based on data from motion sensors (accelerometer – motion) and heart activity (heart rate – heart). The process includes raw data preprocessing, physiologically meaningful feature extraction, and sleep stage prediction using a previously trained machine learning model (RandomForestClassifier).

## 2. Data Loading
   The program starts by loading two text files:

- acceleration.txt – contains accelerometer data: time and acceleration in three axes (x, y, z)
- heartrate.txt – contains heart rate data: time and heart rate values

## 3. Data Cropping (crop_data)
   The crop_data() function aligns the time ranges of the motion and heart streams to ensure they share a common timeframe. This enables synchronized and joint analysis of both signals.

## 4. Motion Signal Processing – build_activity_counts()
   The accelerometer data is processed in several stages:

- Interpolation to a fixed frequency of 50 Hz (uniform sampling),
- Band-pass filtering (3–11 Hz) using a Butterworth filter – selecting only frequencies typical for human physical activity,
- Amplitude binning – the filtered values are converted into discrete levels (128 levels), representing activity intensity,
- Activity counts in 15-second epochs – the maximum value is taken from each second and summed within each epoch,
- Scaling the counts – results are scaled to match the ActiGraph standard range.
- The activity values are then interpolated to 1-second intervals and returned as activity_df.

## 5. Heart Rate Signal Processing – build_heart_rate()
   Heart rate values are:

- Interpolated to 1 Hz frequency,
- Smoothed using a Difference of Gaussians (DoG) filter – capturing both fast and slow fluctuations,
- Scaled – divided by the 90th percentile of absolute values to maintain proportionality and robustness to outliers.

The result is a DataFrame with time and processed heart rate values.

## 6. Feature Extraction (Feature Engineering)
   For each second of the processed signal, the following features are generated:

- activity_count – averaged activity within a 15-second window, smoothed using a Gaussian filter,
- heart_rate_std – standard deviation of heart rate within a 10-minute window (centered around the current second),
- circadian_cycle – value of the cosine function representing the circadian rhythm (24-hour period), with sleepiness peak at 5:00 AM,
- hour_of_sleep – number of hours since the start of the sleep recording, capturing the biological sleep timing.

## 7. Classification
   The input data, in the form of a feature matrix (one row = one second of sleep), is fed into a previously trained RandomForestClassifier.
   The best performing model was a RandomForestClassifier with:

- Tree depth of 10,
- Class weights enabled to handle class imbalance,
- Saved to file as model.joblib and loaded using joblib.load().

It predicts the sleep stage (wake/NREM/REM) for each second and returns the sequence of predicted stages.

## 8. Returning the Results
   The program returns a DataFrame where each row corresponds to one second of sleep and contains the predicted sleep stage (stage).