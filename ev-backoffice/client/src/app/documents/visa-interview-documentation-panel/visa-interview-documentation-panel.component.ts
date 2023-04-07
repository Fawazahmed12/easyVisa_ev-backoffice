import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { User } from '../../core/models/user.model';
import { UserService } from '../../core/services';


@Component({
  selector: 'app-visa-interview-documentation-panel',
  templateUrl: './visa-interview-documentation-panel.component.html',
  styleUrls: [ './visa-interview-documentation-panel.component.scss' ]
})
export class VisaInterviewDocumentationPanelComponent implements OnInit {
  currentUser$: Observable<User>;
  currentUserName$: Observable<string>;

  constructor(
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.currentUser$ = this.userService.currentUser$;
    this.currentUserName$ = this.currentUser$.pipe(
      filter((currentUser) => !!currentUser),
      map((currentUser) => `${currentUser.profile.firstName} ${currentUser.profile.lastName}`),
    );
  }
}
