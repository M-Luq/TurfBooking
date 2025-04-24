import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TurfDTO } from '../model/turfdto.model';
import { Router } from '@angular/router';
import { TurfService } from '../turf.service';
import { CustomerService } from '../../user/customer/customer.service';

@Component({
  selector: 'app-turf-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './turf-list.component.html',
  styleUrls: ['./turf-list.component.css'],
})
export class TurfListComponent implements OnInit {
  turfs: TurfDTO[] = [];
  filteredTurfs: TurfDTO[] = [];
  searchQuery: string = '';
  sportType: string = '';
  minPrice: number | null = null;
  maxPrice: number | null = null;
  sortOption: string = '';
  filter: boolean = false;
  isLoading: boolean = true;
  errorMessage: string = '';
  defaultImagePath = 'assets/turf/';
  showFavorites: boolean = false;
  favoriteTurfIds: number[] = [];

  constructor(
    private turfService: TurfService,
    private router: Router,
    private userService: CustomerService
  ) {}

  ngOnInit() {
    this.loadTurfs();
    this.loadFavoriteTurfs();
  }

  loadTurfs() {
    this.isLoading = true;
    this.errorMessage = '';

    this.turfService.getAllTurfs().subscribe({
      next: (turfs: TurfDTO[]) => {
        this.turfs = turfs;
        this.filteredTurfs = turfs.map((turf) => {
          if (turf.sportType) {
            turf.imageUrl = `${this.defaultImagePath}${turf.sportType}.jpg`;
          } else {
            console.error('Invalid sportType for turf:', turf);
            turf.imageUrl = `${this.defaultImagePath}default.jpg`;
          }
          return turf;
        });
        this.isLoading = false;
        if (this.showFavorites) {
          this.filterFavorites();
        } else {
          this.searchTurfs();
        }
      },
      error: (error) => {
        this.errorMessage = 'Failed to load turfs. Please try again.';
        console.error('Error fetching turfs:', error);
        this.isLoading = false;
      },
    });
  }

  loadFavoriteTurfs() {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.userService.getCustomerById(parseInt(userId)).subscribe({
        next: (customer) => {
          if (customer && customer.favouriteTurfs) {
            this.favoriteTurfIds = customer.favouriteTurfs
              .split(',')
              .filter((id: string) => id)
              .map(Number);
          } else {
            this.favoriteTurfIds = [];
          }
        },
        error: (error) => {
          console.error('Error loading favorite turfs:', error);
        },
      });
    }
  }

  isFilter() {
    this.filter = !this.filter;
    return this.filter;
  }

  searchTurfs() {
    if (this.showFavorites) {
      this.filterFavorites();
      return;
    }
    this.filteredTurfs = this.turfs.filter((turf) => {
      const nameMatch = turf.name.toLowerCase().includes(this.searchQuery.toLowerCase());
      const locationMatch = turf.location.toLowerCase().includes(this.searchQuery.toLowerCase());
      const sportMatch = !this.sportType || turf.sportType === this.sportType;
      const priceMatch = (!this.minPrice || turf.pricePerHour >= this.minPrice) &&
        (!this.maxPrice || turf.pricePerHour <= this.maxPrice);

      return (nameMatch || locationMatch) && sportMatch && priceMatch;
    });
    this.sortTurfs();
  }

  sortTurfs() {
    if (this.sortOption === 'priceAsc') {
      this.filteredTurfs.sort((a, b) => a.pricePerHour - b.pricePerHour);
    } else if (this.sortOption === 'priceDesc') {
      this.filteredTurfs.sort((a, b) => b.pricePerHour - a.pricePerHour);
    } else if (this.sortOption === 'nameAsc') {
      this.filteredTurfs.sort((a, b) => a.name.localeCompare(b.name));
    } else if (this.sortOption === 'nameDesc') {
      this.filteredTurfs.sort((a, b) => b.name.localeCompare(a.name));
    }
  }

  viewTurfDetails(turf: TurfDTO) {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }
    this.router.navigate(['/turf-details', turf.id]);
  }

  trackByTurfId(index: number, turf: TurfDTO): number {
    return turf.id;
  }

  toggleFavorites() {
    this.showFavorites = !this.showFavorites;
    if (this.showFavorites) {
      this.filterFavorites();
    } else {
      this.searchTurfs();
    }
  }

  filterFavorites() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.userService.getCustomerById(parseInt(userId)).subscribe({
      next: (customer) => {
        if (customer && customer.favouriteTurfs) {
          this.filteredTurfs = this.turfs.filter((turf) =>
            customer.favouriteTurfs
              .split(',')
              .filter((id: string) => id)
              .map(Number)
              .includes(turf.id)
          );
        } else {
          this.filteredTurfs = [];
        }
        this.searchTurfs();
      },
      error: (error) => {
        console.error('Error fetching favorite turfs:', error);
        this.errorMessage = 'Failed to load favorite turfs.';
      },
    });
  }

  toggleFavorite(turf: TurfDTO) {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }
    const userIdNumber = parseInt(userId);
    const turfId = turf.id;

    this.userService.getCustomerById(userIdNumber).subscribe((customer) => {
      let favoriteTurfIds: any = [];
      if (customer && customer.favouriteTurfs) {
        favoriteTurfIds = customer.favouriteTurfs
          .split(',')
          .filter((id: string) => id)
          .map(Number);
      }
      if (favoriteTurfIds.includes(turfId)) {
        this.userService.removeFavoriteTurf(userIdNumber, turfId).subscribe(() => {
          this.loadFavoriteTurfs();
        });
      } else {
        this.userService.addFavoriteTurf(userIdNumber, turfId).subscribe(() => {
          this.loadFavoriteTurfs();
        });
      }
    });
  }

  isFavorite(turfId: number): boolean {
    return this.favoriteTurfIds.includes(turfId);
  }
}