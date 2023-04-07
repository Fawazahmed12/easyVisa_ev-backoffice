import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs';

import { AuthService, UserService } from '../../core/services';
import { User } from '../../core/models/user.model';
import { RequestState } from '../../core/ngrx/utils';

@Component({
  selector: 'app-entry',
  templateUrl: './entry.component.html',
  styleUrls: [ './entry.component.scss' ],
})
export class EntryComponent implements OnInit {

  isLoggedIn$: Observable<boolean>;
  logoutRequest$: Observable<RequestState<any>>;
  currentUser$: Observable<User> = this.userService.currentUser$;

  constructor(
    public router: Router,
    public authService: AuthService,
    private userService: UserService,
  ) {
  }


  ngOnInit() {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
    this.logoutRequest$ = this.authService.logoutRequest$;
  }

  goToLogin() {
    if (this.router.url.includes('login')) {
      window.location.href = this.router.url;
      return;
    }
    this.router.navigate([ 'auth', 'login' ]);
  }

  goToRegister() {
    this.router.navigate([ 'auth', 'registration' ]);
  }

  logOut() {
    this.authService.logout();
  }
}
