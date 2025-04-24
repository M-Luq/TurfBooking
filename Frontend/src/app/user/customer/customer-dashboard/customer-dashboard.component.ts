import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookingService } from '../../../booking/booking.service';
import { TurfService } from '../../../turf/turf.service';
import { Payment } from '../../../payment/payment.model';
import { PaymentService } from '../../../payment/payment.service';
import { Booking } from '../../../booking/model/booking.model';
import { TurfDTO } from '../../../turf/model/turfdto.model';
import { CommonModule, DatePipe } from '@angular/common';
import { CustomerService } from '../../customer/customer.service';
import { catchError, of } from 'rxjs';

@Component({
    selector: 'customer-dashboard',
    standalone: true,
    imports: [CommonModule, DatePipe],
    templateUrl: './customer-dashboard.component.html',
    styleUrls: ['./customer-dashboard.component.css'],
})
export class CustomerDashboardComponent implements OnInit {
    isCustomerRegistered: boolean = true;
    username: string | null = localStorage.getItem('username');
    activeSection: string = 'bookings';
    activeAllSection: string = 'allBookings'; // Added for all section sub-navigation
    cbookings: { booking: Booking; turf: TurfDTO | null }[] = [];
    payments: Payment[] = [];
    userId: number = 0;
    allBookings: { booking: Booking; turf: TurfDTO | null }[] = [];
    allPayments: Payment[] = [];

    constructor(
        private router: Router,
        private bookingService: BookingService,
        private turfService: TurfService,
        private paymentService: PaymentService,
        private customerService: CustomerService
    ) {
        const userIdString = localStorage.getItem('userId');
        if (userIdString) {
            this.userId = parseInt(userIdString, 10);
        } else {
            console.error('userId not found in localStorage');
        }
    }

    ngOnInit(): void {
        this.checkCustomerRegistration();
        if (this.isCustomerRegistered) {
            this.fetchBookings();
            this.fetchPayments();
        }
    }

    checkCustomerRegistration(): void {
        this.customerService.isRegistered(this.userId).subscribe({
            next: (registered) => {
                this.isCustomerRegistered = registered;
                if (!this.isCustomerRegistered) {
                    this.navigateToCustomerDetails();
                }
            },
            error: (error) => {
                console.error('Error checking registration:', error);
            },
        });
    }

    fetchBookings(): void {
        this.bookingService.getBookingsByUserId(this.userId).subscribe((bookings) => {
            const pendingBookings = bookings.filter((booking) => booking.status === 'PENDING');
            const confirmedBookings = bookings.filter((booking) => booking.status === 'CONFIRMED');
            const sortedBookings = [...pendingBookings, ...confirmedBookings].sort(
                (a, b) => new Date(b.bookingDateTime).getTime() - new Date(a.bookingDateTime).getTime()
            );
            const lastThreeBookings = sortedBookings.slice(0, 3);

            this.cbookings = [];
            lastThreeBookings.forEach((booking) => {
                this.turfService.getTurfById(booking.turfId).subscribe((turf) => {
                    this.cbookings.push({ booking: booking, turf: turf });
                });
            });
        });
    }

    fetchPayments(): void {
        this.paymentService.getPaymentsByUserId(this.userId).subscribe((payments) => {
            const sortedPayments = payments.sort(
                (a, b) => new Date(b.paymentDate).getTime() - new Date(a.paymentDate).getTime()
            );
            this.payments = sortedPayments.slice(0, 3);
        });
    }

    navigateToCustomerDetails(): void {
        this.router.navigate(['/customer-details']);
    }

    setActiveSection(section: string): void {
        this.activeSection = section;
        if (section === 'all') {
            this.fetchAllBookingsAndPayments();
        }
    }

    setActiveAllSection(section: string): void {
        this.activeAllSection = section;
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

    payBooking(bookingId: number): void {
        this.bookingService.processPayment(bookingId).pipe(
            catchError((error) => {
                console.error('Payment error:', error);
                return of(null);
            })
        ).subscribe((payment) => {
            if (payment) {
                console.log('Payment successful:', payment);
                this.fetchBookings();
                this.fetchPayments();
            }
        });
    }

    cancelBooking(bookingId: number): void {
        this.bookingService.cancelBooking(bookingId).pipe(
            catchError((error) => {
                console.error('Cancellation error:', error);
                return of(null);
            })
        ).subscribe(() => {
            this.fetchBookings();
        });
    }

    refundPayment(bookingId: number): void {
        this.bookingService.processRefund(bookingId).pipe(
            catchError((error) => {
                console.error('Refund error:', error);
                return of(null);
            })
        ).subscribe(() => {
            this.fetchPayments();
            this.fetchBookings();
        });
    }

    isRefundable(paymentDate: string): boolean {
        const paymentTime = new Date(paymentDate).getTime();
        const currentTime = new Date().getTime();
        const oneMinute = 60 * 1000; // 1 minute in milliseconds

        return (currentTime - paymentTime) <= oneMinute;
    }

    fetchAllBookingsAndPayments(): void {
        this.bookingService.getBookingsByUserId(this.userId).subscribe((bookings) => {
            this.allBookings = [];
            bookings.forEach((booking) => {
                this.turfService.getTurfById(booking.turfId).subscribe((turf) => {
                    this.allBookings.push({ booking: booking, turf: turf });
                });
            });
        });

        this.paymentService.getPaymentsByUserId(this.userId).subscribe((payments) => {
            this.allPayments = payments;
        });
    }
}