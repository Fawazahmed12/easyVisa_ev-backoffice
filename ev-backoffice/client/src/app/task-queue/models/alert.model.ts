import {User} from '../../core/models/user.model';

export class Alert {
  id: number;
  subject: string;
  content: string;
  createdOn: string;
  source: string;
  read: boolean;
  starred: boolean;
  userId: number;
  recipientName: string;
}
