// error-page.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-error-page',
  standalone:true,
  imports:[RouterLink,RouterOutlet],
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.css'],
})
export class ErrorPageComponent implements OnInit {
  errorMessage: string = 'An unknown error occurred.';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['message']) {
        this.errorMessage = params['message'];
      }
    });
  }
}