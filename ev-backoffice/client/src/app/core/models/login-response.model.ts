import { Role } from './role.enum';

export interface LoginResponse {
  access_token: string;
  expires_in: number;
  id: number;
  refresh_token: string;
  roles: Role[];
  username: string;
}
