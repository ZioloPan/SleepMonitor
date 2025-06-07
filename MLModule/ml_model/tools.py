import pandas as pd
import numpy as np
from scipy.signal import butter, filtfilt


def build_activity_counts(data):
    # Interpolacja sygnału - Próbkowanie sygnału z osi Z do stałej częstotliwości fs = 50 Hz przy użyciu interpolacji (czyli równomierne próbkowanie danych, nawet jeśli dane wejściowe miały nieregularne odstępy czasowe)
    fs = 50
    time = np.arange(np.amin(data["timestamp"]), np.amax(data["timestamp"]), 1.0 / fs)
    z_data = np.interp(time, data["timestamp"], data["accelerationZ"])

    # Filtrowanie pasmowoprzepustowe - Filtrujesz sygnał pasmowo w zakresie 3–11 Hz — odpowiadające częstotliwościom typowym dla aktywności człowieka. filtfilt: filtracja w przód i wstecz (brak opóźnienia fazowego). Następnie bierzesz moduł wartości (amplitudę)
    cf_low = 3
    cf_hi = 11
    order = 5
    w1 = cf_low / (fs / 2)
    w2 = cf_hi / (fs / 2)
    pass_band = [w1, w2]
    b, a = butter(order, pass_band, 'bandpass')

    z_filt = filtfilt(b, a, z_data)
    z_filt = np.abs(z_filt)
    top_edge = 5
    bottom_edge = 0
    number_of_bins = 128

    # Binning danych - Dzielisz przefiltrowane dane na 128 przedziałów (binów) wartości amplitudy — zakłada się, że to odpowiada poziomom aktywności. Każdy punkt przypisujesz do binu (czyli przekształcasz dane w kategorie liczbowo).
    bin_edges = np.linspace(bottom_edge, top_edge, number_of_bins + 1)
    binned = np.digitize(z_filt, bin_edges)
    epoch = 15

    # Zliczanie w epokach czasowych (15 sekund) - Dzielisz sygnał na epoki (15 sekund) i w każdej epoce: Dzielisz na sekundy, z każdej wybierasz maksimum (czyli najwyższy poziom aktywności w danej sekundzie), Następnie sumujesz te maksimum w ciągu epoki = "count".
    counts = max2epochs(binned, fs, epoch)

    # Skalowanie aktywności - Dopasowanie wartości do skali odpowiadającej ActiGraph (skalowanie i przesunięcie). Negatywne zliczenia są obcinane do 0.
    counts = (counts - 18) * 3.07
    counts[counts < 0] = 0

    # Tworzenie tablicy [czas, zliczenia]
    time_counts = np.linspace(data["timestamp"].min(), data["timestamp"].max(), len(counts))

    # activity_df = pd.DataFrame({
    #     "time": time_counts,
    #     "activity": counts
    # })

    # zinterpolowane wartości co sekundę
    interpolated_timestamps = np.arange(min(time_counts), max(time_counts), 1)
    interpolated_counts = np.interp(interpolated_timestamps, time_counts, counts)
    activity_df = pd.DataFrame({
        "timestamp": interpolated_timestamps,
        "activity": interpolated_counts
    })
    # wartości co 1 sekundę, liczba aktywności w danej sekundzie
    return activity_df


def max2epochs(data, fs, epoch):
    data = data.flatten()

    seconds = int(np.floor(np.shape(data)[0] / fs))
    data = np.abs(data)
    data = data[0:int(seconds * fs)]

    data = data.reshape(fs, seconds, order='F').copy()

    data = data.max(0)
    data = data.flatten()
    N = np.shape(data)[0]
    num_epochs = int(np.floor(N / epoch))
    data = data[0:(num_epochs * epoch)]

    data = data.reshape(epoch, num_epochs, order='F').copy()
    epoch_data = np.sum(data, axis=0)
    epoch_data = epoch_data.flatten()

    return epoch_data


def smooth_gauss(y):
    box_pts = len(y)
    box = np.ones(box_pts) / box_pts
    mu = int(box_pts / 2.0)
    sigma = 50  # seconds

    for ind in range(0, box_pts):
        box[ind] = np.exp(-1 / 2 * (((ind - mu) / sigma) ** 2))

    box = box / np.sum(box)
    sum_value = 0
    for ind in range(0, box_pts):
        sum_value += box[ind] * y[ind]

    return sum_value


def convolve_with_dog(y, box_pts):
    y = y - np.mean(y)
    box = np.ones(box_pts) / box_pts

    mu1 = int(box_pts / 2.0)
    sigma1 = 120

    mu2 = int(box_pts / 2.0)
    sigma2 = 600

    scalar = 0.75

    for ind in range(0, box_pts):
        box[ind] = np.exp(-1 / 2 * (((ind - mu1) / sigma1) ** 2)) - scalar * np.exp(
            -1 / 2 * (((ind - mu2) / sigma2) ** 2))

    y = np.insert(y, 0, np.flip(y[0:int(box_pts / 2)]))  # Pad by repeating boundary conditions
    y = np.insert(y, len(y) - 1, np.flip(y[int(-box_pts / 2):]))
    y_smooth = np.convolve(y, box, mode='valid')

    return y_smooth


# funkcja cosinusa o okresie 24 godzin - jego minimum (max senności) występuje o 5 rano, +1 to wysoa senność, -1 to niska
def cosine_proxy(time):
    sleep_drive_cosine_shift = 5
    seconds_per_hour = 3600
    seconds_per_day = 3600 * 24
    return -1 * np.cos((time - sleep_drive_cosine_shift * seconds_per_hour) *
                       2 * np.pi / seconds_per_day)


def crop_data(motion, heart, labels):
    if labels is None:
        start = max(motion["timestamp"].min(), heart["timestamp"].min())
        end = min(motion["timestamp"].max(), heart["timestamp"].max())

        motion_cropped = motion[(motion["timestamp"] >= start) & (motion["timestamp"] <= end)]
        heart_cropped = heart[(heart["timestamp"] >= start) & (heart["timestamp"] <= end)]
        return motion_cropped, heart_cropped

    start = max(motion["timestamp"].min(), heart["timestamp"].min(), labels["timestamp"].min())
    end = min(motion["timestamp"].max(), heart["timestamp"].max(), labels["timestamp"].max())

    motion_cropped = motion[(motion["timestamp"] >= start) & (motion["timestamp"] <= end)]
    heart_cropped = heart[(heart["timestamp"] >= start) & (heart["timestamp"] <= end)]
    labels_cropped = labels[(labels["timestamp"] >= start) & (labels["timestamp"] <= end)]

    return motion_cropped, heart_cropped, labels_cropped


def get_window(data, feature, epoch):
    window_size = 10 * 30 - 15
    start_time = epoch - window_size
    end_time = epoch + 30 + window_size

    return data[
        (data["timestamp"] > start_time) &
        (data["timestamp"] < end_time)
        ][feature].values


def build_heart_rate(heart_df):
    window_size = 10 * 30 - 15
    timestamps = heart_df["timestamp"]
    heart_rate_values = heart_df["heartRateValue"]

    interpolated_timestamps = np.arange(min(timestamps), max(timestamps), 1)
    interpolated_hr = np.interp(interpolated_timestamps, timestamps, heart_rate_values)

    interpolated_hr = convolve_with_dog(interpolated_hr, window_size)
    scalar = np.percentile(np.abs(interpolated_hr), 90)

    return pd.DataFrame({
        "timestamp": interpolated_timestamps,
        "heart_rate": interpolated_hr / scalar
    })
