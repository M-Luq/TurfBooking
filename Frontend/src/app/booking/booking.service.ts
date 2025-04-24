import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TurfDTO } from '../turf/model/turfdto.model';
import { Booking } from './model/booking.model'; // Create this model if needed

@Injectable({
  providedIn: 'root',
})
export class BookingService {
  private baseUrl = 'http://localhost:8081/bookings'; // Replace with your backend URL

  constructor(private http: HttpClient) {}

  createBooking(
    turfId: number,
    customerId: number,
    bookingRequest: { startTime: string; endTime: string }
  ): Observable<Booking> {
    return this.http.post<Booking>(
      `${this.baseUrl}/book/${turfId}/${customerId}`,
      bookingRequest
    );
  }

  getAvailableSlots(turfId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/getAvailableSlots/${turfId}`);
  }

  getBookingsByUserId(userId: number): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.baseUrl}/user/${userId}`);
  }

  processPayment(bookingId: number): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/pay/${bookingId}`, null);
  }

  processRefund(bookingId: number): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/refund/${bookingId}`, null);
  }

  cancelBooking(bookingId: number): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/cancel/${bookingId}`, null);
}

  getBookingsByOwner(ownerId: number): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.baseUrl}/owner/${ownerId}`);
  }

  getBookingById(bookingId: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.baseUrl}/get/${bookingId}`);
  }
}