import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PredictionService {

  private predictionUrl = 'http://127.0.0.1:5000/predict';  // URL aplikacji Pythonowej

  constructor(private http: HttpClient) {}

  // Wysłanie danych do aplikacji Pythona
  sendPredictionData(heartRateData: any, motionData: any): Observable<any> {
    const requestBody = {
      heart_rate: heartRateData,
      acceleration: motionData,
    };

    return this.http.post<any>(this.predictionUrl, requestBody);
  }
}
