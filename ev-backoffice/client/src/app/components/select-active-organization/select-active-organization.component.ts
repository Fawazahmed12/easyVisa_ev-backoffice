import { Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

import { Organization } from '../../core/models/organization.model';
import { OrganizationService, PackagesService } from '../../core/services';
import { Router } from '@angular/router';

@Component({
  selector: 'app-select-active-organization',
  templateUrl: './select-active-organization.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectActiveOrganizationComponent),
      multi: true
    }
  ],
  styles: [
    `select {
      height: 22px;
    }

    .select-label {
      font-size: 14px;
    }`
  ]
})
@DestroySubscribers()
export class SelectActiveOrganizationComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() isDimmed: boolean = null;
  private subscribers: any = {};

  organizationIdFormControl = new FormControl();
  onChange: (value: string) => void;
  onTouched: () => void;

  organizations$: Observable<Organization[]>;
  activeOrganizationId$: Observable<string>;
  activeOrganization$: Observable<Organization>;

  constructor(
    private organizationService: OrganizationService,
    private packagesService: PackagesService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.organizations$ = this.organizationService.organizations$;
    this.activeOrganizationId$ = this.organizationService.activeOrganizationId$;
    this.activeOrganization$ = this.organizationService.activeOrganization$;
  }

  addSubscribers() {
    this.subscribers.organizationIdSubscription = this.organizationIdFormControl.valueChanges.pipe(
      filter(() => !!this.onChange)
    ).subscribe((value) => {
      this.reloadCurrentRoute();
      return this.onChange(value);
    });
  }

  reloadCurrentRoute() {
    this.packagesService.clearActivePackage();
    const currentUrl = this.router.url;
    const hasIncludesQuestionnaireOrDocuments = currentUrl.includes('questionnaire') || currentUrl.includes('documents');
    if (hasIncludesQuestionnaireOrDocuments) {
      this.router.navigate(['/']);
      return;
    }
    this.router.navigateByUrl(currentUrl);
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  writeValue(value) {
    this.organizationIdFormControl.patchValue(parseInt(value, 10), { emitEvent: false });
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    this.organizationIdFormControl.markAsTouched();
    isDisabled ?
      this.organizationIdFormControl.disable({ emitEvent: false })
      :
      this.organizationIdFormControl.enable({ emitEvent: false });
  }
}
