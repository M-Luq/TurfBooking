export interface TurfDTO {
    id: number;
    name: string;
    ownerId:number;
    location: string;
    sportType: string;
    pricePerHour: number;
    description?: string;
    facilities?: string;
    capacity: number;
    openingTime: string;
    closingTime: string;
    surfaceType?: string;
    imageUrl: string; // Changed to imageUrl, and type is correct.
  }