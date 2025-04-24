import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer, CustomerDTO } from './model/customer.model';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private baseUrl = 'http://localhost:8081/customers'; // Replace with your backend URL

  constructor(private http: HttpClient) {}

  getCustomerById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/get/${id}`);
  }

  isRegistered(customerId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/registered/${customerId}`);
  }

  createCustomer(username: string, customer: CustomerDTO): Observable<CustomerDTO> {
    return this.http.post<CustomerDTO>(`${this.baseUrl}/create/${username}`, customer);
  }

  deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }

//   getBookingsByUserId(userId: number): Observable<Booking[]> {
//     return this.http.get<Booking[]>(`${this.baseUrl}/bookings/user/${userId}`);
//   }

//   getPaymentsByUserId(userId: number): Observable<Payment[]> {
//     return this.http.get<Payment[]>(`${this.baseUrl}/payments/user/${userId}`);
//   }

  updateCustomer(id: number, customer: CustomerDTO): Observable<CustomerDTO> {
    return this.http.put<CustomerDTO>(`${this.baseUrl}/update/${id}`, customer);
  }

  addFavoriteTurf(userId: number, turfId: number): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/${userId}/favorites/${turfId}`,null, { responseType: 'text' as 'json' });
  }

  removeFavoriteTurf(userId: number, turfId: number): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/${userId}/favorites/${turfId}`,{ responseType: 'text' as 'json' });
  }
}