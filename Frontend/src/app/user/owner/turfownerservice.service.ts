// turfowner.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TurfOwner } from './model/turfowner.model';

@Injectable({
  providedIn: 'root',
})
export class TurfOwnerService {
  private baseUrl = 'http://localhost:8081/owners'; // Replace with your actual base URL

  constructor(private http: HttpClient) {}

  createTurfOwner(username: string, turfOwner: TurfOwner): Observable<TurfOwner> {
    return this.http.post<TurfOwner>(`${this.baseUrl}/create/${username}`, turfOwner);
  }

  updateTurfOwner(id: number, turfOwner: TurfOwner): Observable<TurfOwner> {
    return this.http.put<TurfOwner>(`${this.baseUrl}/update/${id}`, turfOwner);
  }

  getTurfOwnerById(id: number): Observable<TurfOwner> {
    return this.http.get<TurfOwner>(`${this.baseUrl}/get/${id}`);
  }

  getAllTurfOwners(): Observable<TurfOwner[]> {
    return this.http.get<TurfOwner[]>(`${this.baseUrl}/getAll`);
  }

  deleteTurfOwner(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }

  updateOwnedTurfs(ownerId: number, turfId: string): Observable<void> {
    const params = new HttpParams()
      .set('ownerId', ownerId.toString())
      .set('turfId', turfId);
    return this.http.put<void>(`${this.baseUrl}/updateOwnedTurfs`, null, { params });
  }

  removeOwnedTurf(ownerId: number, turfId: string): Observable<void> {
    const params = new HttpParams()
      .set('ownerId', ownerId.toString())
      .set('turfId', turfId);
    return this.http.put<void>(`${this.baseUrl}/removeOwnedTurf`, null, { params });
  }

  getTurfIdsByOwnerId(ownerId: number): Observable<number[]> {
    const params = new HttpParams().set('ownerId', ownerId.toString());
    return this.http.get<number[]>(`${this.baseUrl}/turfIds`, { params });
  }

  isRegistered(ownerId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/registered/${ownerId}`);
  }
}