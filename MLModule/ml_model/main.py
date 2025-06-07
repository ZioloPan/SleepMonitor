from flask import Flask, request, jsonify
import pandas as pd
from ml_model.predict import predict_stages

app = Flask(__name__)

data_storage = []


@app.route('/predict', methods=['POST'])
def post_data():
    data = request.get_json()
    if not data:
        return jsonify({"error": "No JSON data provided"}), 400

    hr = pd.DataFrame.from_records(data['heart_rate'])
    acc = pd.DataFrame.from_records(data['acceleration'])
    predictions = predict_stages(acc, hr)
    print(predictions)
    return predictions.to_json(), 200


if __name__ == '__main__':
    app.run(debug=True)
