import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TurfService } from '../../../turf/turf.service';
import { HttpClient } from '@angular/common/http';
import { TurfOwnerService } from '../turfownerservice.service';
import { Router } from '@angular/router';
import { TurfDTO } from '../../../turf/model/turfdto.model';
import { Booking } from '../../../booking/model/booking.model';
import { BookingService } from '../../../booking/booking.service';
import { Payment } from '../../../payment/payment.model';
import { PaymentService } from '../../../payment/payment.service';

@Component({
    selector: 'owner-dashboard',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './owner-dashboard.component.html',
    styleUrls: ['./owner-dashboard.component.css'],
})
export class OwnerDashboardComponent implements OnInit {
    activeSection: string = 'ownedTurfs';
    ownedTurfs: TurfDTO[] = [];
    selectedTurfIdForBookings: number | null = null;
    selectedTurfIdForPayments: number | null = null;
    ownerId: number;
    isOwnerRegistered: boolean = true;
    defaultImagePath = '/assets/turf/';
    ownerBookings: Booking[] = [];
    selectedTurfBookings: Booking[] = [];
    turfPayments: Payment[] = [];
    username: string | null = null;
    ownerPayments: Payment[] = [];

    constructor(
        private turfService: TurfService,
        private ownerService: TurfOwnerService,
        private http: HttpClient,
        private router: Router,
        private bookingService: BookingService,
        private paymentService: PaymentService
    ) {
        const userIdString = localStorage.getItem('userId');
        if (userIdString) {
            this.ownerId = parseInt(userIdString, 10);
        } else {
            console.error('userId not found in localStorage');
            this.ownerId = 0;
        }
        this.username = localStorage.getItem('username');
    }

    ngOnInit() {
        this.checkOwnerRegistration();
        if (this.isOwnerRegistered) {
            this.loadOwnedTurfs();
        }
    }

    checkOwnerRegistration() {
        this.ownerService.isRegistered(this.ownerId).subscribe({
            next: (registered) => {
                this.isOwnerRegistered = registered;
                if (!this.isOwnerRegistered) {
                    this.navigateToOwnerDetails();
                }
            },
            error: (error) => {
                console.error('Error checking registration:', error);
            },
        });
    }

    loadOwnedTurfs() {
        this.turfService.getTurfsByOwnerId(this.ownerId).subscribe({
            next: (turfs) => {
                this.ownedTurfs = turfs.map((turf) => {
                    if (turf.sportType) {
                        const fullImageUrl = `${this.defaultImagePath}${turf.sportType}.jpg`;
                        turf.imageUrl = fullImageUrl;
                    } else {
                        console.error('Invalid sportType for turf:', turf);
                        turf.imageUrl = `${this.defaultImagePath}default.jpg`;
                    }
                    return turf;
                });
            },
            error: (error) => {
                console.error('Error fetching owned turfs:', error);
            },
        });
    }

    showBookings(turfId: number) {
        this.bookingService.getBookingsByOwner(this.ownerId).subscribe({
            next: (bookings) => {
                this.ownerBookings = bookings;
                this.selectedTurfBookings = this.ownerBookings.filter(booking => booking.turfId === turfId);
                this.selectedTurfIdForBookings = turfId;
            },
            error: (error) => {
                console.error('Error fetching bookings for owner:', error);
            },
        });
    }

    showAllBookings() {
        this.bookingService.getBookingsByOwner(this.ownerId).subscribe({
            next: (bookings) => {
                this.ownerBookings = bookings;
                this.selectedTurfBookings = []; // Ensure this is cleared
                this.selectedTurfIdForBookings = null;
            },
            error: (error) => {
                console.error('Error fetching bookings for owner:', error);
            },
        });
    }

    showPayments(turfId: number) {
        this.selectedTurfIdForPayments = turfId;
        this.paymentService.getPaymentsByTurfId(turfId).subscribe({
            next: (payments) => {
                this.turfPayments = payments;
            },
            error: (error) => {
                console.error('Error fetching payments:', error);
            },
        });
    }

    setActiveSection(section: string) {
        this.activeSection = section;
    }

    navigateToOwnerDetails() {
        this.router.navigate(['/owner-details']);
    }

    addTurf() {
        this.router.navigate(['/add-turf']);
    }

    editTurf(turfId: number) {
        this.router.navigate(['/turf-edit', turfId]);
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

    hideTurfBookings() {
        this.selectedTurfBookings = [];
        this.ownerBookings = []; // Clear owner bookings as well
        this.selectedTurfIdForBookings = null;
    }

    showAllPayments() {
        this.paymentService.getPaymentsByOwnerId(this.ownerId).subscribe({
            next: (payments) => {
                this.ownerPayments = payments;
                this.selectedTurfIdForPayments = null;
            },
            error: (error) => {
                console.error('Error fetching all payments:', error);
            },
        });
    }
}