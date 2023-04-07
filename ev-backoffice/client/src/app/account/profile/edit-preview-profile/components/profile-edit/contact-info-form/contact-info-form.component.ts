import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { UserService } from '../../../../../../core/services';
import { Role } from '../../../../../../core/models/role.enum';

import { ProfileEditService } from '../profile-edit.service';

@Component({
  selector: 'app-contact-info-form',
  templateUrl: './contact-info-form.component.html',
  styleUrls: ['./contact-info-form.component.scss'],
})

export class ContactInfoFormComponent implements OnInit, OnDestroy {
  @Input() isOrganization = false;
  isAttorney$: Observable<boolean>;
  isUser$: Observable<boolean>;

  constructor(
    private userService: UserService,
    private profileEditService: ProfileEditService,
  ) {
  }

  get officePhoneFormControl() {
    return this.profileEditService.profileFormGroup.get('officePhone');
  }

  get mobilePhoneFormControl() {
    return this.profileEditService.profileFormGroup.get('mobilePhone');
  }

  get faxNumberFormControl() {
    return this.profileEditService.profileFormGroup.get('faxNumber');
  }

  get homeNumberFormControl() {
    return this.profileEditService.profileFormGroup.get('homeNumber');
  }

  get workNumberFormControl() {
    return this.profileEditService.profileFormGroup.get('workNumber');
  }

  get emailFormControl() {
    return this.profileEditService.profileFormGroup.get('email');
  }

  ngOnInit() {
    this.isAttorney$ = this.userService.hasAccess([Role.ROLE_ATTORNEY]);
    this.isUser$ = this.userService.hasAccess([Role.ROLE_USER]);
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
