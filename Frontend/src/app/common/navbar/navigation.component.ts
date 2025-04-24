import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule, FormsModule],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
})
export class NavigationComponent implements OnInit {

  router = inject(Router);

  isDropdownOpen: boolean = false;
  isAccountDropdownOpen: boolean = false; // New dropdown for account actions
  userRole: string | null = null;


  ngOnInit() {
    this.userRole = this.getRolesFromToken();
  }

  getRolesFromToken(): string | null {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        return decodedToken.roles; // Assuming the role is in 'roles'
      } catch (error) {
        console.error('Error decoding token:', error);
        return null;
      }
    }
    return null;
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  logout(): void {
    localStorage.clear();
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
    this.isAccountDropdownOpen = false; // Close other dropdown if open
  }

  toggleAccountDropdown() {
    this.isAccountDropdownOpen = !this.isAccountDropdownOpen;
    this.isDropdownOpen = false; // Close other dropdown if open
  }

  editProfile() {
    if (localStorage.getItem('roles') === 'TURFOWNER') {
      this.router.navigate(['/owner-details']);
    } else {
      this.router.navigate(['/customer-details']);
    }
    this.isDropdownOpen = false; // Close the dropdown
  }

  changeUsernamePassword() {
    // Navigate to the change username/password page
    this.router.navigate(['/change-credentials']); // Replace with your actual route
    this.isAccountDropdownOpen = false; // Close the dropdown
  }
}