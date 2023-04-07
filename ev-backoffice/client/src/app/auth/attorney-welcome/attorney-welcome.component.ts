import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { OrganizationService } from '../../core/services';

@Component({
  selector: 'app-attorney-welcome',
  templateUrl: './attorney-welcome.component.html',
  styleUrls: ['./attorney-welcome.component.scss'],
})
export class AttorneyWelcomeComponent {

  public sections = [
    {
      title: 'TEMPLATE.AUTH.WELCOME.ITEM_TITLE_1',
      descriptions: [
        'TEMPLATE.AUTH.WELCOME.ITEM_DESCRIPTION_1_1',
        'TEMPLATE.AUTH.WELCOME.CREATE_LAW_PRACTICE_DESC_1',
        'TEMPLATE.AUTH.WELCOME.CREATE_LAW_PRACTICE_DESC_2',
        'TEMPLATE.AUTH.WELCOME.CREATE_LAW_PRACTICE_DESC_3',
        'TEMPLATE.AUTH.WELCOME.CREATE_LAW_PRACTICE_DESC_4',
      ],
      attention: 'TEMPLATE.AUTH.WELCOME.DO_THESE_FIRST',
      arrowTop: true,
    },
    {
      title: 'TEMPLATE.AUTH.WELCOME.ITEM_TITLE_2',
      descriptions: ['TEMPLATE.AUTH.WELCOME.ITEM_DESCRIPTION_2_1', 'TEMPLATE.AUTH.WELCOME.ITEM_DESCRIPTION_2_2'],
    },
    {
      title: 'TEMPLATE.AUTH.WELCOME.ITEM_TITLE_3',
      descriptions: ['TEMPLATE.AUTH.WELCOME.ITEM_DESCRIPTION_3_1'],
    },
    {
      title: 'TEMPLATE.AUTH.WELCOME.ITEM_TITLE_4',
      descriptions: ['TEMPLATE.AUTH.WELCOME.ITEM_DESCRIPTION_4_1'],
    },
    {
      title: 'TEMPLATE.AUTH.WELCOME.ITEM_TITLE_5',
      descriptions: ['TEMPLATE.AUTH.WELCOME.ITEM_DESCRIPTION_5_1'],
      attention: 'TEMPLATE.AUTH.WELCOME.CHECK_THIS_OUT',
      arrowBtmSmall: true,
    },
    {
      title: 'TEMPLATE.AUTH.WELCOME.ITEM_TITLE_6',
      descriptions: ['TEMPLATE.AUTH.WELCOME.ITEM_DESCRIPTION_6_1'],
    }
  ];

  constructor(
    private router: Router,
    private organizationService: OrganizationService,
  ) {
  }

  goToProfile() {
    this.router.navigate(['account', 'profile']);
    this.organizationService.getMenuOrganizations();
  }
}
