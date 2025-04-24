import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../booking.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Booking } from '../model/booking.model';
import { TurfService } from '../../turf/turf.service';
import { TurfDTO } from '../../turf/model/turfdto.model';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'booking-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './booking-create.component.html',
  styleUrls: ['./booking-create.component.css'],
})
export class BookingCreateComponent implements OnInit {
  bookings: { turfId: number; startTime: string; endTime: string }[] = [];
  customerId: number = 0;
  backendErrors: string[] = [];
  bookingDetails: Booking[] = [];
  paymentRequired: boolean = false;
  isLoading: boolean = true;
  turf: TurfDTO | null = null;
  bookingCreated: boolean = false;
  paymentInProgress: boolean = false;
  defaultImagePath: string = '/assets/turf/'; // Define defaultImagePath

  constructor(
    private bookingService: BookingService,
    private route: ActivatedRoute,
    private router: Router,
    private turfService: TurfService
  ) {
    const userIdString = localStorage.getItem('userId');
    if (userIdString) {
      this.customerId = parseInt(userIdString, 10);
    } else {
      console.error('userId not found in localStorage');
      this.customerId = 0;
    }
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['bookings']) {
        this.bookings = JSON.parse(params['bookings']);
        this.createSingleBooking();
      } else {
        this.backendErrors.push('No booking details provided.');
        this.isLoading = false;
      }
    });
  }

  createSingleBooking(): void {
    if (this.bookings.length > 0 && !this.bookingCreated) {
      const firstSlot = this.bookings[0];
      const lastSlot = this.bookings[this.bookings.length - 1];

      const startTime = firstSlot.startTime.split(':')[0] + ':' + firstSlot.startTime.split(':')[1];
      const endTime = lastSlot.endTime.split(':')[0] + ':' + lastSlot.endTime.split(':')[1];
      const turfId = firstSlot.turfId;

      this.turfService.getTurfById(turfId).subscribe({
        next: (turf) => {
          this.turf = turf;

          // Add the logic to set imageUrl here
          if (this.turf && this.turf.sportType) {
            this.turf.imageUrl = `${this.defaultImagePath}${this.turf.sportType}.jpg`;
          } else if (this.turf) {
            console.error('Invalid sportType for turf:', this.turf);
            this.turf.imageUrl = `${this.defaultImagePath}default.jpg`;
          }

          this.bookingService
            .createBooking(turfId, this.customerId, { startTime: startTime, endTime: endTime })
            .subscribe({
              next: (createdBooking) => {
                this.bookingDetails.push(createdBooking);
                this.paymentRequired = true;
                this.isLoading = false;
                this.bookingCreated = true;
              },
              error: (error) => {
                this.backendErrors.push('Failed to create booking.');
                console.error('Error creating booking:', error);
                if (error && error.error && error.error.message) {
                  this.backendErrors.push(error.error.message);
                }
                this.isLoading = false;
              },
            });
        },
        error: (error) => {
          this.backendErrors.push('Failed to fetch turf details.');
          console.error('Error fetching turf details:', error);
          this.isLoading = false;
        },
      });
    } else if (this.bookingCreated) {
      this.isLoading = false;
    } else {
      this.backendErrors.push('No booking details provided.');
      this.isLoading = false;
    }
  }

  payNow(): void {
    if (this.bookingDetails.length > 0 && !this.paymentInProgress) {
      this.paymentInProgress = true;
      let allPaymentsSuccessful = true;

      const paymentPromises = this.bookingDetails.map(booking => {
        return firstValueFrom(this.bookingService.processPayment(booking.id)); // Use firstValueFrom
      });

      Promise.all(paymentPromises)
        .then(() => {
          this.router.navigate(['/customer-dashboard']);
        })
        .catch(error => {
          allPaymentsSuccessful = false;
          this.backendErrors.push('One or more payments failed.');
          console.error('Payment error:', error);
        })
        .finally(() => {
          this.paymentInProgress = false;
        });
    } else if (this.paymentInProgress) {
      this.backendErrors.push('Payment is already in progress.');
    } else {
      this.backendErrors.push('No bookings to process payment for.');
    }
  }

  formatTime(timeString: string): string {
    const parts = timeString.split(':');
    if (parts.length === 3) {
        let hours = parseInt(parts[0], 10);
        const minutes = parts[1];
        const ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12;
        return `${hours}:${minutes} ${ampm}`;
    }
    return timeString;
}
}