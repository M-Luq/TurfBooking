export interface Payment {
    id: number;
    bookingId: number;
    userId: number;
    turfId: number;
    amount: number;
    paymentDate: string;
    status: string;
  }