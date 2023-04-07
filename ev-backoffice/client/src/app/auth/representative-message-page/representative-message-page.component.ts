import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { UserService } from '../../core/services';
import { AttorneyType } from '../../core/models/attorney-type.enum';

@Component({
  selector: 'app-representative-message-page',
  templateUrl: './representative-message-page.component.html',
  styleUrls: [ './representative-message-page.component.scss' ],
})
export class RepresentativeMessagePageComponent implements OnInit {

  isMemberOfLawFirm$: Observable<boolean>;

  constructor(
    private router: Router,
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.isMemberOfLawFirm$ = this.userService.registrationRepresentativeType$.pipe(
      map(representativeType => representativeType === AttorneyType.MEMBER_OF_A_LAW_FIRM)
    );
  }

  goToSelectRepresentativeType() {
    this.router.navigate(['auth', 'select-representative-type']);
  }

  goToStandartEvCharges() {
    this.router.navigate(['auth', 'standard-ev-charges']);
  }
}
