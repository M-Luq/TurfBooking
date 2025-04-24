import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turf } from './model/turf.model'; // Import your Turf model
import { TurfDTO } from './model/turfdto.model';

@Injectable({
  providedIn: 'root'
})
export class TurfService {
  private apiUrl = 'http://localhost:8081/turfs'; // Replace with your backend API URL

  constructor(private http: HttpClient) {}

  getAllTurfs(): Observable<TurfDTO[]> {
    return this.http.get<TurfDTO[]>(`${this.apiUrl}/getAll`);
  }

  getTurfById(id: number): Observable<TurfDTO> {
    return this.http.get<TurfDTO>(`${this.apiUrl}/getTurf/${id}`);
  }

  getTurfsByOwnerId(ownerId: number): Observable<TurfDTO[]> {
    return this.http.get<TurfDTO[]>(`${this.apiUrl}/getTurf/owner/${ownerId}`);
  }

  addTurf(turf: Turf, ownerId: number): Observable<Turf> {
    return this.http.post<Turf>(`${this.apiUrl}/add/${ownerId}`, turf);
  }

  updateTurf(turf: Turf, id: number): Observable<Turf> {
    return this.http.put<Turf>(`${this.apiUrl}/update/${id}`, turf);
  }

  deleteTurf(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

//   searchTurfs(location?: string, sportType?: string, minPrice?: number, maxPrice?: number): Observable<Turf[]> {
//     let params: any = {};
//     if (location) {
//       params.location = location;
//     }
//     if (sportType) {
//       params.sportType = sportType;
//     }
//     if (minPrice) {
//       params.minPrice = minPrice;
//     }
//     if (maxPrice) {
//       params.maxPrice = maxPrice;
//     }

//     return this.http.get<Turf[]>(`${this.apiUrl}/search`, { params: params });
//   }

  // Add other methods as needed (e.g., booking-related methods)
}