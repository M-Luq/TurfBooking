// login.component.ts
import { Component } from '@angular/core';
import { AuthRequest } from '../model/auth-request.model';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SecurityService } from '../secuirty.service';
import { jwtDecode } from 'jwt-decode';
import { ApiResponse } from '../model/api-response.model';
import { ErrorHandlerService } from '../../common/shared-services/error-handler.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  authRequest: AuthRequest = { username: '', password: '' };
  message: string = '';
  validationErrors: { [key: string]: string } = {};
  isLoading: boolean = false;

  constructor(private authService: SecurityService, private router: Router, private errorHandler: ErrorHandlerService) {}
  
  ngOnInit() {
    // Clear token on login page load
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('roles');
    console.log('Token removed on login page load.');
  }
  
  login(loginForm: NgForm) {
    if (loginForm.valid) {
      this.isLoading = true;
      this.authService
        .loginUser(loginForm.value)
        .pipe(
          catchError((error) => {
            this.handleError(error);
            this.isLoading = false;
            return of(null);
          })
        )
        .subscribe({
          next: (response: ApiResponse<string> | null) => { // Corrected type here
            this.isLoading = false;
            if (response && response.data) { // Added null check
              const token = response.data;
              localStorage.setItem('token', token);

              try {
                const decodedToken: any = jwtDecode(token);
                localStorage.setItem('userId', decodedToken.userId.toString());
                localStorage.setItem('username', decodedToken.sub);
                localStorage.setItem('roles', decodedToken.roles);

                if (response.status === 200) {
                  const roles: string[] = decodedToken.roles;
                  if (roles.includes('TURFOWNER')) {
                    this.router.navigate(['/owner-dashboard']);
                  } else if (roles.includes('CUSTOMER')) {
                    this.router.navigate(['/customer-dashboard']);
                  } else {
                    this.message = 'Role not recognized.';
                  }
                } else {
                  this.message = 'Unexpected response status.';
                }
              } catch (error) {
                console.error('Error decoding token:', error);
                this.message = 'Error decoding token.';
              }
            } else if (response) { // handles the case of response being null, or response data being null.
              this.message = response.message;
            }
          },
          error: (error) => {
            this.isLoading = false;
            this.handleError(error);
          },
        });
    }
  }

  private handleError(error: any) {
    if (error.status === 400 && error.error && error.error.data) {
      this.validationErrors = error.error.data;
      this.message = error.error.message;
    } else if (error.status === 404 && error.error && error.error.message) {
      this.message = error.error.message;
    } else if (error.status === 401 && error.error && error.error.message) {
      this.message = error.error.message;
    } else {
      this.errorHandler.handleError(error).subscribe();
    }
  }
}