import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';

import { Role } from '../../../../../../core/models/role.enum';
import { UserService } from '../../../../../../core/services';


@Component({
  selector: 'app-login-credentials',
  templateUrl: './login-credentials.component.html',
})

export class LoginCredentialsComponent implements OnInit {
  isUser$: Observable<boolean>;

  constructor(
    private userService: UserService,
  ) {}

  ngOnInit() {
    this.isUser$ = this.userService.hasAccess([Role.ROLE_USER]);
  }

}
