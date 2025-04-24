import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { CustomerService } from '../customer.service';
import { CustomerDTO } from '../model/customer.model';
import { Router, ActivatedRoute } from '@angular/router';
import { SecurityService } from '../../../security/secuirty.service';
import { ErrorHandlerService } from '../../../common/shared-services/error-handler.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-customer-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customer-details.component.html',
  styleUrls: ['./customer-details.component.css'],
})
export class CustomerDetailsComponent implements OnInit {
  customer: CustomerDTO = {
    name: '',
    email: '',
    phoneNumber: '',
    address: '',
  };
  customerId: number;
  username: string;
  isUpdatedMode: boolean = false;
  backendErrors: string[] = [];
  isLoading: boolean = false;

  constructor(
    private customerService: CustomerService,
    private router: Router,
    private authservice: SecurityService,
    private errorHandler: ErrorHandlerService,
    private route: ActivatedRoute
  ) {
    const userIdString = localStorage.getItem('userId');
    if (userIdString) {
      this.customerId = parseInt(userIdString, 10);
    } else {
      console.error('userId not found in localStorage');
      this.customerId = 0;
    }
    const usernameString = localStorage.getItem('username');
    if (usernameString) {
      this.username = usernameString;
    } else {
      console.error('username not found in localStorage');
      this.username = '';
    }
  }

  ngOnInit() {
    this.isLoading = true;
    this.route.queryParams.subscribe(params => {
      this.isUpdatedMode = params['mode'] === 'edit';
    });

    this.authservice.getEmail(this.username)
      .pipe(
        catchError((error) => {
          this.handleError(error);
          return of('');
        })
      )
      .subscribe((email) => {
        this.customer.email = email;
        this.checkCustomerRegistration();
      });
  }

  checkCustomerRegistration() {
    if (this.customerId && this.customerId !== 0) {
      this.customerService.isRegistered(this.customerId)
        .pipe(
          catchError((error) => {
            this.handleError(error);
            this.isLoading = false;
            return of(false);
          })
        )
        .subscribe((registered) => {
          if (registered) {
            this.isUpdatedMode=true
            this.fetchCustomerDetails();
          } else {
            this.isLoading = false;
          }
        });
    } else {
      this.isLoading = false;
    }
  }

  fetchCustomerDetails() {
    if (this.customerId && this.customerId !== 0) {
      this.customerService.getCustomerById(this.customerId)
        .pipe(
          catchError((error) => {
            this.handleError(error);
            if (error.status !== 500) {
              this.isLoading = false;
            }
            return of(null);
          })
        )
        .subscribe((owner) => {
          this.isLoading = false;
          if (owner) {
            this.customer = owner;
          }
        });
    } else {
      this.isLoading = false;
    }
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.isLoading = true;
      const operation = this.isUpdatedMode
        ? this.customerService.updateCustomer(this.customerId, this.customer)
        : this.customerService.createCustomer(this.username, this.customer);

      operation
        .pipe(
          catchError((error) => {
            this.handleError(error);
            this.isLoading = false;
            return of(null);
          })
        )
        .subscribe(() => {
          this.isLoading = false;
          if (this.backendErrors.length === 0) {
            this.router.navigate(['/customer-dashboard']);
            this.backendErrors = [];
          }
        });
    }
  }

  deleteAccount() {
    if (confirm('Are you sure you want to delete your account?')) {
      this.isLoading = true;
      this.customerService.deleteCustomer(this.customerId)
        .pipe(
          catchError((error) => {
            this.handleError(error);
            this.isLoading = false;
            return of(null);
          })
        )
        .subscribe(() => {
          this.isLoading = false;
          localStorage.clear();
          this.router.navigate(['/']);
        });
    }
  }

  private handleError(error: any) {
    this.backendErrors = [];
    if (error && error.error) {
      if (error.status === 500) {
        if (typeof error.error === 'string') {
          this.backendErrors.push(error.error);
        } else if (typeof error.error === 'object' && error.error.message) {
          this.backendErrors.push(error.error.message);
        } else {
          this.backendErrors.push('Internal Server Error: No specific message provided.');
        }
      } else if (error.status === 400 || error.status === 404 || error.status === 401) {
        if (typeof error.error === 'string') {
          this.backendErrors.push(error.error);
        } else if (typeof error.error === 'object' && error.error.message) {
          this.backendErrors.push(error.error.message);
        } else if (error.error.errors) {
          for (const field in error.error.errors) {
            if (error.error.errors.hasOwnProperty(field)) {
              this.backendErrors.push(error.error.errors[field]);
            }
          }
        } else {
          this.backendErrors.push('An unexpected error occurred.');
        }
      } else {
        this.errorHandler.handleError(error).subscribe();
      }
    } else if (typeof error === 'string') {
      this.backendErrors.push(error);
    } else {
      this.errorHandler.handleError(error).subscribe();
    }
  }
}