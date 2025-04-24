// booking.model.ts

export interface Booking {
    id: number;
    turfId: number;
    turfName: string;
    userId: number;
    bookingDateTime: string; // Use string for LocalDateTime
    startTime: string; // Use string for LocalTime
    endTime: string; // Use string for LocalTime
    slotInfo: string;
    status: BookingStatus;
    turfPrice: number;
  }

  export enum BookingStatus {
    CONFIRMED = 'CONFIRMED',
    PENDING = 'PENDING',
    CANCELLED = 'CANCELLED',
    CANCELLEDNP = 'CANCELLEDNP', // Add this line
}
  
  
  
 