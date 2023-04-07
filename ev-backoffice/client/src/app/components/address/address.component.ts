import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { startWith, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { states } from '../../core/models/states';
import { countries } from '../../core/models/countries';
import { UserService } from '../../core/services';
import { Role } from '../../core/models/role.enum';

@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
})
@DestroySubscribers()
export class AddressComponent implements AddSubscribers, OnInit, OnDestroy {
  @Input() formGroup: FormGroup;
  @Input() showCountrySelect = true;
  @Input() submitted = false;
  @Input() disabled = false;
  @Input() primaryTextStyle = false;
  @Input() smallMarginStyle = false;
  @Input() required = false;
  @Input() showToolTips = false;
  @Input() col3Label = false;
  @Input() col4Label = false;

  isUser$: Observable<boolean>;

  countries = countries;
  selectedUS = 'UNITED_STATES';
  states = states;

  private subscribers: any = {};

  get countryControl() {
    return this.formGroup.get('country');
  }

  get address1Control() {
    return this.formGroup.get('line1');
  }

  get address2Control() {
    return this.formGroup.get('line2');
  }

  get cityControl() {
    return this.formGroup.get('city');
  }

  get stateControl() {
    return this.formGroup.get('state');
  }

  get provinceControl() {
    return this.formGroup.get('province');
  }

  get zipControl() {
    return this.formGroup.get('zipCode');
  }

  get postalCodeControl() {
    return this.formGroup.get('postalCode');
  }
  constructor(
    private userService: UserService,
  ) {
    this.isUser$ = this.userService.hasAccess([Role.ROLE_USER]);
  }
  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
  }

  addSubscribers() {
    this.subscribers.countrySubscription = this.countryControl.valueChanges.pipe(
      startWith(this.countryControl.value),
    ).pipe(withLatestFrom(this.isUser$)
    ).subscribe(([country, isUser]) => {
      if (this.disabled) {
        this.formGroup.disable({emitEvent: false});
      } else if (country === null) {
        this.stateControl.disable({emitEvent: false});
        this.cityControl.disable({emitEvent: false});
        this.address2Control.disable({emitEvent: false});
        this.address1Control.disable({emitEvent: false});
        this.provinceControl.disable({emitEvent: false});
        this.postalCodeControl.disable({emitEvent: false});
      } else if (country === this.selectedUS) {
        this.provinceControl.disable({emitEvent: false});
        this.postalCodeControl.disable({emitEvent: false});
        this.stateControl.enable({emitEvent: false});
        this.zipControl.enable({emitEvent: false});
        this.cityControl.enable({emitEvent: false});
        this.address2Control.enable({emitEvent: false});
        this.address1Control.enable({emitEvent: false});
      } else if (!!country && country !== this.selectedUS) {
        this.provinceControl.enable({emitEvent: false});
        this.postalCodeControl.enable({emitEvent: false});
        this.cityControl.enable({emitEvent: false});
        this.address2Control.enable({emitEvent: false});
        this.address1Control.enable({emitEvent: false});
        this.stateControl.disable({emitEvent: false});
        this.zipControl.disable({emitEvent: false});
      } else if (isUser) {
        this.provinceControl.disable({emitEvent: false});
        this.postalCodeControl.disable({emitEvent: false});
        this.stateControl.disable({emitEvent: false});
        this.zipControl.disable({emitEvent: false});
      }
    });

  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
