import { Routes } from '@angular/router';
import { RegisterComponent } from './security/register/register.component';
import { LoginComponent } from './security/login/login.component';
import { TurfListComponent } from './turf/turf-list/turf-list.component';
import { AddTurfComponent } from './turf/add-turf/add-turf.component';
import { ErrorPageComponent } from './common/error/error-page.component';
import { AuthGuard } from './guards/auth.guard';
import { TurfEditComponent } from './turf/turf-edit/turf-edit.component';
import { OwnerDashboardComponent } from './user/owner/owner-dashboard/owner-dashboard.component';
import { OwnerDetailsComponent } from './user/owner/owner-details/owner-details.component';
import { ChangePasswordComponent } from './user/change-password/change-password.component';
import { CustomerDetailsComponent } from './user/customer/customer-details/customer-details.component';
import { CustomerDashboardComponent } from './user/customer/customer-dashboard/customer-dashboard.component';
import { BookingCreateComponent } from './booking/booking-create/booking-create.component';
import { TurfDetailsComponent } from './turf/turf-details/turf-details.component';
import { RoleGuard } from './guards/role.guard';

    export const routes: Routes = [
      { path: 'owner-details', component: OwnerDetailsComponent ,canActivate:[AuthGuard,RoleGuard],data: { roles: ['TURFOWNER'] }},
      { path: 'turf-details/:id', component: TurfDetailsComponent },
      { path: 'add-turf', component: AddTurfComponent,canActivate:[AuthGuard,RoleGuard],data: { roles: ['TURFOWNER'] } },
      { path: 'register', component: RegisterComponent },
      { path: 'login', component: LoginComponent },
      { path: 'booking-create', component: BookingCreateComponent ,canActivate:[AuthGuard,RoleGuard],data: { roles: ['CUSTOMER'] }},
      { path: 'owner-dashboard', component: OwnerDashboardComponent,canActivate:[AuthGuard,RoleGuard],data: { roles: ['TURFOWNER'] } },
      { path: 'error', component: ErrorPageComponent },
      {path:'turf-list',component:TurfListComponent},
      { path: 'turf-edit/:id', component: TurfEditComponent,canActivate:[AuthGuard,RoleGuard],data: { roles: ['TURFOWNER'] } },
      {path:'change-credentials',component:ChangePasswordComponent,canActivate:[AuthGuard,RoleGuard],data: { roles: ['TURFOWNER', 'CUSTOMER'] }},
      {path:'customer-details',component:CustomerDetailsComponent,canActivate:[AuthGuard,RoleGuard],data: { roles: ['CUSTOMER'] }},
      {path:'customer-dashboard',component:CustomerDashboardComponent,canActivate:[AuthGuard,RoleGuard],data: { roles: ['CUSTOMER'] }},
      { path: '', redirectTo: '/login', pathMatch: 'full' },

    ];