from ml_model.tools import *
from joblib import load
from keras.models import load_model
import pandas as pd


def predict_stages(motion, heart):
    motion, heart = crop_data(motion, heart, None)
    activity = build_activity_counts(motion)
    heart_rate = build_heart_rate(heart)

    epochs = heart_rate["timestamp"].tolist()
    rows = []
    for epoch in [e for e in epochs if e >= 0]:
        rows.append({
            "second_of_sleep": epoch,
            "activity_count": smooth_gauss(get_window(activity, "activity", epoch)),
            "heart_rate_std": np.std(get_window(heart_rate, "heart_rate", epoch)),
            "circadian_cycle": cosine_proxy(epoch - epochs[0]),
            "hour_of_sleep": (epoch - epochs[0]) / 3600.0
        })
    df = pd.DataFrame(rows)
    df.set_index('second_of_sleep', inplace=True)
    print(df)

    clf_loaded = load('model.joblib')
    df["stage"] = clf_loaded.predict(df)

    mapping = {0: "wake", 1: "NREM", 2: "REM"}
    df["stage"] = df["stage"].map(mapping)
    return df["stage"]

