import { AfterContentInit, Component, OnDestroy, OnInit, Input } from '@angular/core';

import { combineLatest } from 'rxjs';
import { distinctUntilChanged, filter, map, tap, withLatestFrom } from 'rxjs/operators';
import { DestroySubscribers } from 'ngx-destroy-subscribers';
import { Store } from '@ngrx/store';

import { forIn, isEqual } from 'lodash-es';

import { OrganizationService, UserService } from '../../core/services';
import { Role } from '../../core/models/role.enum';
import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';
import { ShowPersonalDataPopUp } from '../../core/ngrx/user/user.actions';
import { State } from '../../auth/ngrx/state';


@Component({
  selector: 'app-warning-personal-page',
  templateUrl: './warning-personal-page.component.html',
})

@DestroySubscribers()
export class WarningPersonalPageComponent implements OnInit, AfterContentInit, OnDestroy {
  @Input() notShowForAdmin = false;
  private subscribers: any = {};

  constructor(
    private organizationService: OrganizationService,
    private userService: UserService,
    private store: Store<State>,
  ) {
  }

  ngOnInit() {
    console.log(`${this.constructor.name} Init`);
  }

  ngAfterContentInit() {
    this.subscribers.isShowViewSubscription = combineLatest([
      this.organizationService.currentRepresentativeId$.pipe(distinctUntilChanged(isEqual)),
      this.userService.currentUserRoles$.pipe(distinctUntilChanged(isEqual)),
      this.organizationService.organizations$.pipe(
        filter(organizations => !!organizations.length),
        map(organizations => !!organizations.length)
      )
    ]).pipe(
      filter(([currentRepresentativeId, , ]) => currentRepresentativeId !== undefined),
      withLatestFrom(
        this.organizationService.currentPosition$.pipe(distinctUntilChanged(isEqual)),
        this.userService.isCurrentRepresentativeMe$.pipe(distinctUntilChanged(isEqual)),
      ),
      map(([[, userRoles, hasOrg], position, isMe]: [[number, Role[], boolean], EmployeePosition, boolean]) => {
        const isUser = userRoles.some(role => role === Role.ROLE_USER);
        const showForPositions = [EmployeePosition.EMPLOYEE, EmployeePosition.TRAINEE, EmployeePosition.MANAGER];
        const isShowForPosition = showForPositions.some(showPosition => showPosition === position);
        return (!hasOrg || isUser || isMe || (isShowForPosition && !hasOrg));
      }),
    ).subscribe(res => {
      if (!res && !this.notShowForAdmin) {
        this.store.dispatch(new ShowPersonalDataPopUp());
      }
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
    forIn(this.subscribers, (val, ) => val.unsubscribe && val.unsubscribe());
  }
}
