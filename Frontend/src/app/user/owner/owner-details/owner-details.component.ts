import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { TurfOwnerService } from '../turfownerservice.service';
import { TurfOwner } from '../model/turfowner.model';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SecurityService } from '../../../security/secuirty.service';
import { ErrorHandlerService } from '../../../common/shared-services/error-handler.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'owner-details',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './owner-details.component.html',
  styleUrls: ['./owner-details.component.css'],
})
export class OwnerDetailsComponent implements OnInit {
  owner: TurfOwner = {
    name: '',
    phoneNumber: '',
    address: '',
    businessContact: '',
    businessAddress: '',
    email: '',
  };
  ownerId: number;
  backendErrors: string[] = [];
  username: string;
  isUpdateMode: boolean = false;
  isLoading: boolean = false;

  constructor(
    private turfOwnerService: TurfOwnerService,
    private router: Router,
    private auth: SecurityService,
    private errorHandler: ErrorHandlerService,
    private route: ActivatedRoute
  ) {
    const userIdString = localStorage.getItem('userId');
    if (userIdString) {
      this.ownerId = parseInt(userIdString, 10);
    } else {
      console.error('userId not found in localStorage');
      this.ownerId = 0;
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
      this.isUpdateMode = params['mode'] === 'edit';
    });

    this.auth.getEmail(this.username)
      .pipe(
        catchError((error) => {
          this.handleError(error);
          return of('');
        })
      )
      .subscribe((email) => {
        this.owner.email = email;
        this.checkOwnerRegistration();
      });
  }

  checkOwnerRegistration() {
    if (this.ownerId && this.ownerId !== 0) {
      this.turfOwnerService.isRegistered(this.ownerId)
        .pipe(
          catchError((error) => {
            this.handleError(error);
            this.isLoading = false;
            return of(false);
          })
        )
        .subscribe((registered) => {
          if (registered) {
            this.isUpdateMode=true
            this.fetchOwnerDetails();
          } else {
            this.isLoading = false;
          }
        });
    } else {
      this.isLoading = false;
    }
  }

  fetchOwnerDetails() {
    if (this.ownerId && this.ownerId !== 0) {
      this.turfOwnerService.getTurfOwnerById(this.ownerId)
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
            this.owner = owner;
          }
        });
    } else {
      this.isLoading = false;
    }
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.isLoading = true;
      const operation = this.isUpdateMode
        ? this.turfOwnerService.updateTurfOwner(this.ownerId, this.owner)
        : this.turfOwnerService.createTurfOwner(this.username, this.owner);

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
            this.router.navigate(['/owner-dashboard']);
            this.backendErrors = [];
          }
        });
    }
  }

  deleteAccount() {
    if (confirm('Are you sure you want to delete your account?')) {
      this.isLoading = true;
      this.turfOwnerService.deleteTurfOwner(this.ownerId)
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