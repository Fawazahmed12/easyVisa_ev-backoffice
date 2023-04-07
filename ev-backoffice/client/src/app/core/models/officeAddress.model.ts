export interface OfficeAddress {
  line1: string;
  line2: string | null;
  city: string;
  country: string;
  state?: string;
  province?: string;
  zipCode?: number | string;
  postalCode?: string;
}
