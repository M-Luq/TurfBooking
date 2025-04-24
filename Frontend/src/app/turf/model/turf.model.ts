export interface Turf {
  name: string;
  location: string;
  sportType: string;
  pricePerHour: number;
  description?: string;
  facilities?: string;
  capacity: number;
  openingTime: string; // Assuming time is stored as string 'HH:mm'
  closingTime: string; // Assuming time is stored as string 'HH:mm'
  surfaceType?: string;
}