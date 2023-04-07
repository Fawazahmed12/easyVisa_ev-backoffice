export interface Review {
  id: number;
  reviewerId?: number;
  packageId: number;
  representativeId: number;
  rating: number;
  title: string;
  review: string;
  reviewer: string;
  reply?: string;
  read?: boolean;
  dateCreated: string;
  petitioner: string;
}
