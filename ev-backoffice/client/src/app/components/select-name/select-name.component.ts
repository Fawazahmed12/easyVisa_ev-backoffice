import { ChangeDetectorRef, Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

import { OrganizationService, UserService } from '../../core/services';
import { Attorney, AttorneyMenu } from '../../core/models/attorney.model';
import { Organization } from '../../core/models/organization.model';
import { EmployeeStatusValues } from '../../account/permissions/models/employee-status.enum';

@Component({
  selector: 'app-select-name',
  templateUrl: './select-name.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectNameComponent),
      multi: true
    }
  ],
  styles: [
    `select{
      height: 22px;
    }
    .select-label{
      font-size: 14px;
    }`
  ]
})
@DestroySubscribers()
export class SelectNameComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() isDimmed: boolean = null;
  @Input() changeColor: boolean;
  @Input() selectRepOption = false;
  private subscribers: any = {};

  representativeIdFormControl = new FormControl();
  onChange: (value: string) => void;
  onTouched: () => void;

  activeOrganization$: Observable<Organization>;
  representatives$: Observable<Attorney[]>;
  representativesMenu$: Observable<AttorneyMenu[]>;

  EmployeeStatusValues = EmployeeStatusValues;

  constructor(
    private userService: UserService,
    private organizationService: OrganizationService,
    private cdr: ChangeDetectorRef
  ) {
  }

  ngOnInit() {
    this.cdr.detectChanges();
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.representatives$ = this.organizationService.representatives$;
    this.representativesMenu$ = this.organizationService.representativesMenu$;
  }

  addSubscribers() {
    this.subscribers.representativeIdSubscription = this.representativeIdFormControl.valueChanges.pipe(
      filter(() => !!this.onChange)
    ).subscribe((value) => this.onChange(value));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  writeValue(value) {
    this.representativeIdFormControl.patchValue(value, {emitEvent: false});
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    this.representativeIdFormControl.markAsTouched();
    isDisabled ?
      this.representativeIdFormControl.disable({emitEvent: false})
      :
      this.representativeIdFormControl.enable({emitEvent: false});
  }
}
