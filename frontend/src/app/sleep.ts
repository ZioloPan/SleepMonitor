import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SleepService {
  private apiUrl = '/api/v1'; // Proxy do backendu

  constructor(private http: HttpClient) {}

  getSleepPhases(from: number, to: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/sleep_stage?from=${from}&to=${to}`);
  }

  getHeartRateData(from: number, to: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/heart_rate?from=${from}&to=${to}`);
  }

  getMotionData(from: number, to: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/acceleration?from=${from}&to=${to}`);
  }
}
