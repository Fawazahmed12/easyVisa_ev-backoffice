import { Component } from '@angular/core';

import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: [ './registration.component.scss' ]
})
export class RegistrationComponent {

  canShowFindAnAttorney = !environment.hideFeatures.registrationFindAnAttorney;
  variants = [
    {
      title: 'TEMPLATE.AUTH.REGISTRATION.PETITIONER_TITLE',
      text: 'TEMPLATE.AUTH.REGISTRATION.PETITIONER_TEXT',
      btnLabel: 'TEMPLATE.AUTH.REGISTRATION.FIND_ATTORNEY_BTN',
      routerLink: environment.marketingSiteLink,
      display: this.canShowFindAnAttorney
    },
    {
      title: 'TEMPLATE.AUTH.REGISTRATION.ATTORNEY_TITLE',
      text: 'TEMPLATE.AUTH.REGISTRATION.ATTORNEY_TEXT',
      btnLabel: 'TEMPLATE.AUTH.REGISTRATION.ATTORNEY_REGISTRATION_BTN',
      routerLink: '/auth/attorney-sign-up',
      display: true
    },
  ];

  constructor(
  ) {
  }
}
