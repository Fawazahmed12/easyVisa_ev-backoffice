import { Invite } from '../../account/models/invite.model';
import { Education } from '../../account/profile/edit-preview-profile/models/education.model';

export class Profile {
  easyVisaId: string = null;
  email: string = null;
  firstName: string = null;
  id: number = null;
  lastName: string = null;
  middleName?: string = null;
  username: string = null;
  activePackageId: number = null;
  newFirmInviteDetails?: Invite;
  education?: Education[];

  constructor(obj) {
    this.populateObject(obj);
  }

  populateObject(obj) {
    for (const key in this) {
      if (typeof obj[key] !== 'undefined') {
        this[key] = obj[key];
      }
    }
  }
}
