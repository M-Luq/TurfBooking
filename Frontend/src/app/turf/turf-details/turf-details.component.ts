import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TurfDTO } from '../model/turfdto.model';
import { TurfService } from '../turf.service';
import { BookingService } from '../../booking/booking.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-turf-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './turf-details.component.html',
  styleUrls: ['./turf-details.component.css'],
})
export class TurfDetailsComponent implements OnInit {
  turf: TurfDTO | null = null;
  availableSlots: string[] = [];
  selectedSlots: string[] = [];
  errorMessage: string = '';
  defaultImagePath: string = 'assets/turf/'; // Set your default image path here

  constructor(
    private route: ActivatedRoute,
    private turfService: TurfService,
    private bookingService: BookingService,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.params.subscribe((params) => {
      const turfId = +params['id'];
      if (turfId) {
        this.turfService.getTurfById(turfId).subscribe({
          next: (turf) => {
            this.turf = this.setImageUrl(turf); // Set image URL here
            this.fetchAvailableSlots(turfId);
          },
          error: (error) => {
            console.error('Error fetching turf details:', error);
            this.errorMessage = 'Failed to load turf details.';
          },
        });
      }
    });
  }

  setImageUrl(turf: TurfDTO): TurfDTO {
    if (turf.sportType) {
      turf.imageUrl = `${this.defaultImagePath}${turf.sportType}.jpg`;
    } else {
      console.error('Invalid sportType for turf:', turf);
      turf.imageUrl = `${this.defaultImagePath}default.jpg`;
    }
    return turf;
  }

  fetchAvailableSlots(turfId: number) {
    this.bookingService.getAvailableSlots(turfId).subscribe({
      next: (slots) => {
        this.availableSlots = slots;
      },
      error: (error) => {
        console.error('Error fetching available slots:', error);
        this.errorMessage = 'Failed to fetch available slots.';
      },
    });
  }

  selectSlot(slot: string): void {
    if (this.selectedSlots.length === 0) {
      this.selectedSlots.push(slot);
    } else {
      if (this.isAdjacent(this.selectedSlots[this.selectedSlots.length - 1], slot)) {
        this.selectedSlots.push(slot);
      } else if (this.isAdjacent(this.selectedSlots[0], slot)) {
        this.selectedSlots.unshift(slot);
      } else {
        this.selectedSlots = [slot];
      }
    }
  }

  isAdjacent(slot1: string, slot2: string): boolean {
    const time1End = this.parseSlotTime(slot1.split(' - ')[1]);
    const time2Start = this.parseSlotTime(slot2.split(' - ')[0]);
    const time1Start = this.parseSlotTime(slot1.split(' - ')[0]);
    const time2End = this.parseSlotTime(slot2.split(' - ')[1]);

    return time1End === time2Start || time1Start === time2End;
  }

  parseSlotTime(time: string): number {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
  }

  bookTurf() {
    if (this.turf && this.selectedSlots.length > 0) {
        const convertedSlots = this.reverseFormatTimeSlots(this.selectedSlots);
        const bookings = convertedSlots.map((slot) => {
            const startTime = slot.split(' - ')[0];
            const endTime = slot.split(' - ')[1];
            return {
                turfId: this.turf!.id,
                startTime: startTime,
                endTime: endTime,
            };
        });

        this.router.navigate(['/booking-create'], {
            queryParams: { bookings: JSON.stringify(bookings) },
        });
    } else {
        this.errorMessage = 'Please select at least one slot.';
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

formatTimeSlots(slots: string[]): string[] {
  return slots.map(slot => {
      let [start, end] = slot.split(" - ");
      let convertToNormalTime = (time: string): string => {
          let [hours, minutes] = time.split(":").map(Number);
          let period = hours >= 12 ? "PM" : "AM";
          hours = hours % 12 || 12; // Convert 0 to 12 for 12 AM
          return `${hours}:${minutes.toString().padStart(2, '0')} ${period}`;
      };
      return `${convertToNormalTime(start)} - ${convertToNormalTime(end)}`;
  });
}

reverseFormatTimeSlots(slots: string[]): string[] {
  return slots.map(slot => {
      let [start, end] = slot.split(" - ");
      let convertTo24HourTime = (time: string): string => {
          let [timePart, period] = time.split(" ");
          let [hours, minutes] = timePart.split(":").map(Number);
          if (period === "PM" && hours !== 12) {
              hours += 12;
          } else if (period === "AM" && hours === 12) {
              hours = 0;
          }
          return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
      };
      return `${convertTo24HourTime(start)} - ${convertTo24HourTime(end)}`;
  });
}


}