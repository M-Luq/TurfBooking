import { Component } from '@angular/core';
import { NgForm, FormsModule } from '@angular/forms';
import { SecurityService } from '../../security/secuirty.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'change-password',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css'],
})
export class ChangePasswordComponent {
  errorMessage: string | null = null;
  successMessage: string | null = null;
  newPassword = '';
  confirmPassword = '';
  passwordMismatch = false;

  constructor(private authService: SecurityService, private router: Router) {}

  onSubmit(form: NgForm) {
    if (form.valid) {
      if (this.newPassword !== this.confirmPassword) {
        this.passwordMismatch = true;
        return;
      }
      this.passwordMismatch = false;

      const credentials = {
        currentPassword: 'dummyCurrentPassword', // Backend should ignore this
        newPassword: this.newPassword,
      };

      this.authService.changeCredentials(credentials).subscribe({
        next: (response) => {
          this.successMessage = 'Password changed successfully.';
          this.errorMessage = null;
          form.resetForm();
          this.newPassword = '';
          this.confirmPassword = '';
        },
        error: (error) => {
          this.errorMessage = error?.error?.message || 'An unexpected error occurred.';
          this.successMessage = null;
        },
      });
    }
  }

  returnToDashboard() {
    this.router.navigate(['/owner-dashboard']);
  }

  ngDoCheck() {
    if (this.newPassword !== '' && this.confirmPassword !== '') {
      this.passwordMismatch = this.newPassword !== this.confirmPassword;
    }
  }
}