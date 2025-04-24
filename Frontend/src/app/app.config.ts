// app.config.ts
import { ApplicationConfig, ErrorHandler, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './common/interceptor/auth.interceptor'; // Import the functional interceptor
import { ErrorHandlerService } from './common/shared-services/error-handler.service';
import { JWT_OPTIONS, JwtHelperService } from '@auth0/angular-jwt';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])), // Use the functional interceptor
    {provide: ErrorHandler, useClass: ErrorHandlerService },
    JwtHelperService,
    { provide: JWT_OPTIONS, useValue: {}},
    ErrorHandlerService 
  ],
};