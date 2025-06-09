import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SleepService {
  private apiUrl = 'http://192.168.146.210:8080/api/v1'; // Proxy do backendu

  constructor(private http: HttpClient) {}

  getNights(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/sleep_stage/nights`);
  }

  getSleepPhases(nightId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/sleep_stage/night/${nightId}`);
  }

  getHeartRateData(nightId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/heart_rate/night/${nightId}`);
  }
}
