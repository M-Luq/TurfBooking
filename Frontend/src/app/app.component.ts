// app.component.ts
import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { NavigationComponent } from './common/navbar/navigation.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit() {
    // Initial background check based on current URL
    this.setBackground(this.router.url);

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.setBackground(event.url);
      }
    });
  }

  private setBackground(url: string) {
    const baseUrl = url.split('?')[0]; // Extract base URL

    if (['/login', '/register', '/'].includes(baseUrl)) {
      document.body.className = 'auth-background';
    } else if (['/turf-list'].includes(baseUrl)) {
      document.body.className = 'turf-background';
    } else {
      document.body.className = 'default-background';
    }
  }
}