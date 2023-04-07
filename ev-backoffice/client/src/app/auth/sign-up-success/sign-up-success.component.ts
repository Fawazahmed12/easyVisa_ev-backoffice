import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs';

import { Attorney } from '../../core/models/attorney.model';

import { SignUpService } from '../services';


@Component({
  selector: 'app-sign-up-success',
  templateUrl: './sign-up-success.component.html',
})

export class SignUpSuccessComponent {
  signUpInfo$: Observable<Attorney> = this.signUpService.attorneySignUpInfo$;
  constructor(
    private signUpService: SignUpService,
    private router: Router,
  ) {}

  redirectToSignUp() {
    this.router.navigate(['auth', 'attorney-sign-up']);
  }

  redirectToLoginPage() {
    this.router.navigate(['auth', 'login']);
  }
}
