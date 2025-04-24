import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './model/user.model';
import { AuthRequest } from './model/auth-request.model';
import { ApiResponse } from './model/api-response.model';
import { ChangePasswordRequest } from './model/change-password-request.model';

@Injectable({
  providedIn: 'root',
})
export class SecurityService {
  private baseUrl = 'http://localhost:8081/auth';

  constructor(private http: HttpClient) {}

  registerUser(user: User): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.baseUrl}/new`, user);
  }

  loginUser(authRequest: AuthRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.baseUrl}/authenticate`, authRequest);
  }
  
  getEmail(username: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/getEmail/${username}`, { responseType: 'text' })
  }

  changeCredentials(credentials: {
    currentPassword: string;
    newPassword: string;
  }): Observable<ApiResponse<string>> {
    const requestBody: ChangePasswordRequest = {
      newPassword: credentials.newPassword,
    };

    return this.http.put<ApiResponse<string>>(
      `${this.baseUrl}/update`,
      requestBody
    );
  }
}