import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TurfService } from '../turf.service';
import { FormsModule, NgForm } from '@angular/forms';
import { Turf } from '../model/turf.model';
import { NgIf, TitleCasePipe } from '@angular/common';

@Component({
  selector: 'add-turf',
  standalone: true,
  imports: [FormsModule, TitleCasePipe, NgIf],
  templateUrl: './add-turf.component.html',
  styleUrls: ['./add-turf.component.css'],
})
export class AddTurfComponent implements OnInit {
  turf: Turf = {
    name: '',
    location: '',
    sportType: '',
    pricePerHour: 0,
    description: '',
    facilities: '',
    capacity: 8,
    openingTime: '',
    closingTime: '',
    surfaceType: '',
  };

  openingTime12: string = '';
  closingTime12: string = '';

  backendErrors: string[] = [];
  ownerId: number;
  imageUrl: string | null = null;
  isSubmitting: boolean = false;
  sportTypes: string[] = ['cricket', 'football', 'hockey', 'tennis', 'basketball','badminton'];

  constructor(private turfService: TurfService, private router: Router) {
    const userIdString = localStorage.getItem('userId');
    if (userIdString) {
      this.ownerId = parseInt(userIdString, 10);
    } else {
      console.error('userId not found in localStorage');
      this.ownerId = 0;
    }
  }

  ngOnInit() {
    this.updateImage();
  }

  updateImage() {
    if (this.turf.sportType) {
      this.imageUrl = `assets/turf/${this.turf.sportType}.jpg`;
    } else {
      this.imageUrl = null;
    }
  }

  convert12to24() {
    if (this.openingTime12) {
      this.turf.openingTime = this.convertTime(this.openingTime12);
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
    let hoursInt = parseInt(hours, 10);
  
    if (modifier === 'PM' && hoursInt !== 12) {
      hoursInt += 12;
    } else if (modifier === 'AM' && hoursInt === 12) {
      hoursInt = 0;
    }
  
    return `${hoursInt.toString().padStart(2, '0')}:${minutes}`;
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.isSubmitting = true;
      this.convert12to24();
      this.turfService.addTurf(this.turf, this.ownerId).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigate(['/owner-dashboard']);
          this.backendErrors = [];
        },
        error: (error) => {
          this.isSubmitting = false;
          this.backendErrors = [];
          if (error && error.error && error.error.message) {
            this.backendErrors.push(error.error.message);
          } else if (error && error.error && error.error.errors) {
            for (const field in error.error.errors) {
              if (error.error.errors.hasOwnProperty(field)) {
                this.backendErrors.push(error.error.errors[field]);
              }
            }
          } else {
            this.backendErrors.push('An unexpected error occurred.');
          }
        },
      });
    }
  }

  onSportTypeChange() {
    this.updateImage();
  }
}