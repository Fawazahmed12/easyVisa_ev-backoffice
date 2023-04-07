import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  catchError, debounceTime,
  filter,
  pluck, skip,
  switchMap,
  take, withLatestFrom,
} from 'rxjs/operators';
import { EMPTY, Observable, Subject } from 'rxjs';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ConfigDataService, ModalService, NotificationsService, OrganizationService, UserService } from '../../../core/services';
import { Role } from '../../../core/models/role.enum';
import { ConfirmButtonType } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { FeeDetails } from '../../../core/models/fee-details.model';
import { PaymentWarningComponent } from '../../../components/payment-warning/payment-warning.component';

import { FinancialDetails } from '../../models/financial-details.model';

import { FinancialService } from '../financial.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-financial-main',
  templateUrl: './financial-main.component.html',
})

@DestroySubscribers()
export class FinancialMainComponent implements OnInit, AddSubscribers, OnDestroy {

  canShowReferralBonuses = !environment.hideFeatures.financialDSHReferralBonuses;

  @ViewChild('clientRevenue', {static: true}) clientRevenue;
  @ViewChild('articleBonuses', {static: true}) articleBonuses;
  @ViewChild('referralBonus', {static: true}) referralBonus;

  findUserId$: Observable<number>;
  currentUserId$: Observable<number>;
  isCurrentRepresentativeMe$: Observable<boolean>;
  feeDetails$: Observable<FeeDetails>;
  financialDetails$: Observable<FinancialDetails>;
  openLearnMoreModalSubject$: Subject<{ body: string; header: string }> = new Subject<{ body: string; header: string }>();

  ROLE_EV = [Role.ROLE_OWNER];
  ROLE_ATTORNEY = Role.ROLE_ATTORNEY;

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
    private configDataService: ConfigDataService,
    private organizationService: OrganizationService,
    private notificationsService: NotificationsService,
    private activatedRoute: ActivatedRoute,
    private financialService: FinancialService,
    private router: Router,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.showPaymentWarning();
    this.findUserId$ = this.userService.findUserId$;
    this.feeDetails$ = this.configDataService.feeDetails$;
    this.financialDetails$ = this.financialService.financialDetails$;
    this.currentUserId$ = this.userService.currentUser$.pipe(
      filter((user) => !!user),
      pluck('profile', 'id')
    );
    this.isCurrentRepresentativeMe$ = this.userService.isCurrentRepresentativeMe$;
  }

  addSubscribers() {
    this.subscribers.openProspectiveClientsModalSubscription = this.openLearnMoreModalSubject$
    .subscribe((data) => this.openLearnMoreModal(data.body, data.header));

    this.subscribers.queryParamsFinancialSubscription = this.activatedRoute.queryParams.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
      take(1),
    ).subscribe(([params, [repId, orgId]]) => this.formGroup.patchValue({
      organizationId: parseInt(params?.organizationId, 10) || orgId,
      representativeId: parseInt(params?.representativeId, 10) || repId,
    }, {emitEvent: false}));

    this.subscribers.currentRepIdSubscription = this.organizationService.currentRepIdOrgId$.pipe(
      skip(1),
      withLatestFrom(this.userService.isCurrentRepresentativeMe$),
    ).subscribe(([[representativeId, organizationId], ]) => this.formGroup.patchValue({representativeId, organizationId}));

    this.subscribers.financialFormGroupSubscription = this.formGroup.valueChanges.pipe(
      filter(() => !!this.representativeIdFormControl.value),
      debounceTime(1),
      withLatestFrom(
        this.organizationService.isAdmin$,
        this.isCurrentRepresentativeMe$
      ),
      filter(([, isAdmin, isMe]) => isAdmin || isMe),
      switchMap(() => this.financialService.getFinancialDetails(this.formGroup.getRawValue()).pipe(
          catchError(() => {
            this.router.navigate([]);
            return EMPTY;
          })
        ))
    ).subscribe(() => this.addQueryParamsToUrl(this.formGroup.getRawValue()));
  }

  ngOnDestroy() {
    this.notificationsService.showComponent$.next(null);
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

  openClientRevenueModal() {
    this.openLearnMoreModalSubject$.next(
      {
        body: this.clientRevenue,
        header: 'TEMPLATE.DASHBOARD.FINANCIAL.CLIENT_REVENUE'
      }
    );
  }

  openArticleBonusesModal() {
    this.openLearnMoreModalSubject$.next(
      {
        body: this.articleBonuses,
        header: 'TEMPLATE.DASHBOARD.ARTICLES.ARTICLE_BONUSES_MODAL.HEADER'
      }
    );
  }

  openReferralBonusModal(){
    this.openLearnMoreModalSubject$.next(
      {
        body: this.referralBonus,
        header: 'TEMPLATE.DASHBOARD.FINANCIAL.REFERRAL_BONUSES'
      }
    );
  }

  openLearnMoreModal(body, header) {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header,
      body,
      buttons,
      centered: true,
      size: 'lg',
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  gotoInviteColleagues(){
    this.modalService.closeAllModals();
    this.router.navigate(['/dashboard/financial/invite-colleagues']);
  }

  showPaymentWarning() {
    if (!this.notificationsService.getIsShowedPaymentWarning()) {
      this.notificationsService.showComponent$.next(PaymentWarningComponent);
      this.notificationsService.setIsShowedPaymentWarning(true);
    }
  }
}
