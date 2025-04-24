// error-handler.service.ts
import { Injectable, ErrorHandler, Injector } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { throwError, Observable } from 'rxjs'; // Import throwError and Observable

@Injectable()
export class ErrorHandlerService implements ErrorHandler {
  constructor(private injector: Injector) {}

  handleError(error: any): Observable<string> { // Return Observable<string>
    const router = this.injector.get(Router);

    let errorMessage = 'An unknown error occurred.';

    if (error instanceof HttpErrorResponse) {
      // Server-side or network error
      console.error('Backend returned code', error.status, error.message);

      switch (error.status) {
        case 0:
          errorMessage = 'Server is unreachable. Please try again later.';
          break;
        case 401:
          errorMessage = 'Unauthorized. Please log in again.';
          break;
        case 403:
          errorMessage = 'Forbidden. You do not have permission to access this resource.';
          break;
        case 404:
          errorMessage = 'Resource not found.';
          break;
        case 409:
          errorMessage = 'Conflict: The request could not be completed.';
          break;
        case 503:
          errorMessage = 'Service Unavailable. Please try again later.';
          break;
        default:
          if (error.error && error.error.message) {
            errorMessage = error.error.message;
          } else {
            errorMessage = `Backend returned code ${error.status}.`;
          }
      }
    } else {
      // Client-side error
      console.error('An error occurred:', error);
      errorMessage = error.message ? error.message : error.toString();
    }
    // Navigate to the error page with the error message as a query parameter
    router.navigate(['/error'], { queryParams: { message: errorMessage } });

    return throwError(errorMessage); // Return the error message as an observable
  }
}