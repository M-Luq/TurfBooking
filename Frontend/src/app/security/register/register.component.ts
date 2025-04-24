import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SecurityService } from '../secuirty.service';
import { ApiResponse } from '../model/api-response.model';
import { ErrorHandlerService } from '../../common/shared-services/error-handler.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  message: string = '';
  validationErrors: { [key: string]: string } = {};
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: SecurityService,
    private router: Router,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
      roles: ['CUSTOMER'],
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;

    if (password !== confirmPassword) {
      return { passwordMismatch: true };
    }

    return null;
  }

  register() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.authService
        .registerUser(this.registerForm.value)
        .pipe(
          catchError((error) => {
            this.handleError(error);
            this.isLoading = false;
            return of(null);
          })
        )
        .subscribe({
          next: (response: ApiResponse<string> | null) => {
            this.isLoading = false;
            if (response) {
              this.message = response.message;
              if (response.status === 201) {
                this.router.navigate(['/login']);
              }
            }
          },
        });
    }
  }

  private handleError(error: any) {
    if (error.status === 400 && error.error && error.error.data) {
      this.validationErrors = error.error.data;
      this.message = error.error.message;
    } else if (error.status === 409 && error.error && error.error.message) {
      this.message = error.error.message;
      this.validationErrors = {}; // Clear previous validation errors
    } else {
      this.errorHandler.handleError(error).subscribe();
    }
  }
}