import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';

import { SetRegistrationRepresentativeType } from '../../../../../../core/ngrx/user/user.actions';
import { RegistrationStatus } from '../../../../../../core/models/registration-status.enum';
import { State } from '../../../../../../core/ngrx/state';

@Component({
  selector: 'app-change-position',
  templateUrl: './change-position.component.html',
})

export class ChangePositionComponent {

  constructor(
    private store: Store<State>,
    private router: Router,
  ) {

  }

  redirectToStandardEvCharges() {
    this.store.dispatch(new SetRegistrationRepresentativeType(RegistrationStatus.CONVERT_TO_ATTORNEY));
    this.router.navigate(['auth', 'standard-ev-charges']);
  }
}
