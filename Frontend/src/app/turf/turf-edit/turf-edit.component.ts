import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TurfDTO } from '../model/turfdto.model';
import { TurfService } from '../turf.service';

@Component({
  selector: 'app-turf-edit',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './turf-edit.component.html',
  styleUrls: ['./turf-edit.component.css'],
})
export class TurfEditComponent implements OnInit {
  turf: TurfDTO = {
    id: 0,
    sportType: '',
    name: '',
    location: '',
    pricePerHour: 0,
    description: '',
    facilities: '',
    capacity: 0,
    openingTime: '',
    closingTime: '',
    surfaceType: '',
    ownerId: 0,
    imageUrl: '',
  };
  turfId: number = 0;
  backendErrors: string[] = [];
  isSubmitting: boolean = false;
  sportTypes: string[] = ['cricket', 'football', 'badminton', 'tennis'];
  defaultImagePath = '/assets/turf/';
  openingTime12: string = '';
  closingTime12: string = '';

  constructor(
    private turfService: TurfService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.turfId = +params['id'];
      if (this.turfId) {
        this.loadTurfDetails(this.turfId);
      }
    });
  }

  loadTurfDetails(id: number): void {
    this.turfService.getTurfById(id).subscribe(
      (turf) => {
        this.turf = turf;
        this.updateImageUrl();
        this.openingTime12 = this.convert24to12(this.turf.openingTime);
        this.closingTime12 = this.convert24to12(this.turf.closingTime);
      },
      (error) => {
        console.error('Error loading turf details:', error);
        this.backendErrors.push('Failed to load turf details.');
      }
    );
  }

  onSubmit(turfForm: NgForm): void {
    if (turfForm.valid) {
      this.isSubmitting = true;
      this.convert12to24();
      this.turfService.updateTurf(this.turf, this.turfId).subscribe({
        next:() => {
          this.isSubmitting = false;
          this.router.navigate(['/owner-dashboard']);
        },
        error:(error) => {
          this.isSubmitting = false;
          this.backendErrors = [];
          if (error && error.error && error.error.message) {
            this.backendErrors.push(error.error.message);
          } else {
            this.backendErrors.push('An unexpected error occurred.');
          }
        }
    });
    }
  }

  deleteTurf(): void {
    if (confirm('Are you sure you want to delete this turf?')) {
      this.turfService.deleteTurf(this.turfId).subscribe({
        next:() => {
          this.router.navigate(['/owner-dashboard']);
        },
        error:(error) => {
          console.error('Error deleting turf:', error);
          this.backendErrors.push('Failed to delete turf.');
        }
    });
    }
  }

  onSportTypeChange(): void {
    this.updateImageUrl();
  }

  updateImageUrl(): void {
    if (this.turf.sportType) {
      this.turf.imageUrl = `${this.defaultImagePath}${this.turf.sportType}.jpg`;
    } else {
      this.turf.imageUrl = `${this.defaultImagePath}default.jpg`;
    }
  }

  convert12to24() {
    console.log("openingTime12:", this.openingTime12); // Check input value
    if (this.openingTime12) {
      this.turf.openingTime = this.convertTime(this.openingTime12);
      console.log("turf.openingTime:", this.turf.openingTime); // Check 24-hour time
    }
    if (this.closingTime12) {
      this.turf.closingTime = this.convertTime(this.closingTime12);
    }
  }

  convertTime(time12: string): string {
    const timeRegex = /^([1-9]|1[0-2]):([0-5][0-9])\s(AM|PM)$/i;
    if (!timeRegex.test(time12)) {
      return '';
    }
    const [time, modifier] = time12.toUpperCase().split(' ');
    let [hours, minutes] = time.split(':');

    if (hours === '12') {
      hours = '00';
    }

    if (modifier === 'PM') {
      hours = `${parseInt(hours, 10) + 12}`;
    }

    // Pad hours with a leading zero if necessary
    hours = hours.padStart(2, '0');

    return `${hours}:${minutes}`;
  }

  convert24to12(time24: string): string {
    if (!time24) return '';
    const [hours, minutes] = time24.split(':');
    let hoursInt = parseInt(hours, 10);
    const modifier = hoursInt >= 12 ? 'PM' : 'AM';
    hoursInt = hoursInt === 0 ? 12 : hoursInt > 12 ? hoursInt - 12 : hoursInt;
    const hoursStr = hoursInt.toString().padStart(2, '0');
    return `${hoursStr}:${minutes} ${modifier}`;
  }
}