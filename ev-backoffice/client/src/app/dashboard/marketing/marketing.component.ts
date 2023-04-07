import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY, Observable, Subject } from 'rxjs';
import {
  catchError, debounceTime,
  filter,
  skip,
  switchMap,
  take,
  withLatestFrom
} from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ModalService, OrganizationService, UserService } from '../../core/services';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';

import { MarketingDetails } from '../models/marketing-details.model';

import { MarketingService } from './marketing.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-marketing',
  templateUrl: './marketing.component.html',
})
@DestroySubscribers()
export class MarketingComponent implements OnInit, AddSubscribers, OnDestroy {

  canShowProspectiveClients = !environment.hideFeatures.marketingDSHProspectiveClients;
  canShowPhoneNumberClients = !environment.hideFeatures.marketingDSHPhoneNumberClients;

  @ViewChild('prospectiveClients', { static: true }) prospectiveClients;
  @ViewChild('phoneNumberClients', { static: true }) phoneNumberClients;

  marketingDetails$: Observable<MarketingDetails>;
  findUserId$: Observable<number>;
  activeOrganizationId$: Observable<string>;
  openLearnMoreModalSubject$: Subject<string> = new Subject<string>();

  formGroup: FormGroup;

  private subscribers: any = {};

  get representativeIdFormControl() {
    return this.formGroup.get('representativeId');
  }

  get organizationIdFormControl() {
    return this.formGroup.get('organizationId');
  }

  constructor(
    private userService: UserService,
    private modalService: ModalService,
    private organizationService: OrganizationService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private marketingService: MarketingService,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.findUserId$ = this.userService.findUserId$;
    this.marketingDetails$ = this.marketingService.marketingDetails$;
    this.activeOrganizationId$ = this.organizationService.activeOrganizationId$;
  }

  addSubscribers() {
    this.subscribers.openProspectiveClientsModalSubscription = this.openLearnMoreModalSubject$.pipe(
      switchMap((body) => this.openLearnMoreModal(body)))
    .subscribe();

    this.subscribers.queryParamsMarketingSubscription = this.activatedRoute.queryParams.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
      take(1),
    ).subscribe(([params, [repId, orgId]]) => this.formGroup.patchValue({
        organizationId: parseInt(params?.organizationId, 10) || orgId,
        representativeId: parseInt(params?.representativeId, 10) || repId,
      }, {emitEvent: false}));

    this.subscribers.currentRepIdMarketingSubscription = this.organizationService.currentRepIdOrgId$.pipe(
      skip(1),
      withLatestFrom(this.userService.isCurrentRepresentativeMe$),
    ).subscribe(([[representativeId, organizationId], ]) => this.formGroup.patchValue({representativeId, organizationId}));

    this.subscribers.marketingFormGroupSubscription = this.formGroup.valueChanges.pipe(
      filter(() => !!this.representativeIdFormControl.value),
      debounceTime(1),
      withLatestFrom(
        this.organizationService.isAdmin$,
        this.userService.isCurrentRepresentativeMe$
      ),
      filter(([, isAdmin, isMe]) => isAdmin || isMe),
      switchMap(() => this.marketingService.getMarketingDetails(this.formGroup.getRawValue()
         ).pipe(
          catchError(() => {
            this.router.navigate([]);
            return EMPTY;
          })
        ))
    ).subscribe(() => this.addQueryParamsToUrl(this.formGroup.getRawValue()));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      representativeId: new FormControl(),
      organizationId: new FormControl(),
    });
  }

  addQueryParamsToUrl(params?) {
    this.router.navigate(['./'], {relativeTo: this.activatedRoute, queryParams: {...params}});
  }

  openProspectiveClients() {
    this.openLearnMoreModalSubject$.next(this.prospectiveClients);
  }

  openPhoneNumberClients() {
    this.openLearnMoreModalSubject$.next(this.phoneNumberClients);
  }

  openLearnMoreModal(body) {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header: body === this.prospectiveClients
        ? 'TEMPLATE.DASHBOARD.ARTICLES.MARKETING.PROSPECTIVE_CLIENTS'
        : 'TEMPLATE.DASHBOARD.ARTICLES.MARKETING.PHONE_NUMBERS_CLIENTS',
      body,
      buttons,
      centered: true,
      size: 'lg',
    }).pipe(
      catchError(() => EMPTY)
    );
  }
}
