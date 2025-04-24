import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment } from './payment.model';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private baseUrl = 'http://localhost:8081/payments';

  constructor(private http: HttpClient) {}

  getPaymentsByUserId(userId: number, status?: string): Observable<Payment[]> {
    let url = `${this.baseUrl}/user/${userId}`;
    if (status) {
      url += `?status=${status}`;
    }
    return this.http.get<Payment[]>(url);
  }

  getPaymentsByTurfId(turfId: number, status?: string): Observable<Payment[]> {
    let url = `${this.baseUrl}/turf/${turfId}`;
    if (status) {
      url += `?status=${status}`;
    }
    return this.http.get<Payment[]>(url);
  }

  getPaymentsByTurfIds(turfIds: number[], status?: string): Observable<Payment[]> {
    let url = `${this.baseUrl}/turfs?turfIds=${turfIds.join(',')}`;
    if (status) {
      url += `&status=${status}`;
    }
    return this.http.get<Payment[]>(url);
  }

  getPaymentsByOwnerId(ownerId: number, status?: string): Observable<Payment[]> {
    let url = `${this.baseUrl}/owner/${ownerId}`;
    if (status) {
        url += `?status=${status}`;
    }
    return this.http.get<Payment[]>(url);
  }

  processPayment(bookingId: number, userId: number, turfId: number, amount: number): Observable<Payment> {
    const paymentData = {
      bookingId: bookingId,
      userId: userId,
      turfId: turfId,
      amount: amount,
    };
    return this.http.post<Payment>(`${this.baseUrl}/process`, paymentData);
  }

  refundPayment(bookingId: number): Observable<Payment> {
    return this.http.post<Payment>(`${this.baseUrl}/refund/${bookingId}`, {});
  }
}