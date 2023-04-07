import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import {filter, map, pluck, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { Subject } from 'rxjs';
import { combineLatest } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EMPTY, ReplaySubject } from 'rxjs';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';
import { Email } from '../../core/models/email.model';
import {
  ConfigDataService,
  EmailsService,
  EmailTemplatesService,
  ModalService,
  NotificationsService,
  PackagesService,
  TaxesService
} from '../../core/services';
import {OkButton, OkButtonLg} from '../../core/modals/confirm-modal/confirm-modal.component';
import { RequestState } from '../../core/ngrx/utils';
import { FileInfo } from '../../core/models/file-info.model';
import {
  EmailTemplateVariablesModalComponent,
} from '../../core/modals/email-template-variables-modal/email-template-variables-modal.component';
import { FileTypeBrowser } from '../../core/models/file-type-browser.enum';
import { Package } from '../../core/models/package/package.model';
import { EstimatedTax } from '../../core/models/estimated-tax.model';
import { PackageStatus } from '../../core/models/package/package-status.enum';
import { TaxTypes } from '../../core/models/tax-types.enum';
import { checkAllowedFileType } from '../../shared/utils/check-allowed-file-type';
import { validateFileSize } from '../../shared/utils/validate-file-size';
import { noWhitespaceValidator } from '../../auth/validators/no-white-space.validator';
import { FeeDetails } from '../../core/models/fee-details.model';
import { ActivePackageComponent } from '../../components/active-package/active-package.component';
import { FeeSchedule } from '../../core/models/fee-schedule.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-email-to-package-applicants',
  templateUrl: './email-to-package-applicants.component.html',
  styleUrls: ['./email-to-package-applicants.component.scss'],
})
@DestroySubscribers()
export class EmailToPackageApplicantsComponent implements OnInit, OnDestroy, AddSubscribers {
  @ViewChild('retainerAgreementPopUp', { static: true }) retainerAgreementPopUp;

  emailType$: Observable<string>;
  title$: Observable<string>;
  package$: Observable<Package>;
  feeDetails$: Observable<FeeDetails>;
  emailPostRequest$: Observable<RequestState<Email>>;
  emailPutRequest$: Observable<RequestState<Email>>;
  file$: Observable<FileInfo | any>;
  showWarning$: Observable<boolean>;
  showSendBtnDescription$: Observable<boolean>;
  showResendBtn$: Observable<boolean>;
  isFileNotAllowed$: Subject<boolean> = new Subject<boolean>();
  isFileSizeNotAllowed$: Subject<boolean> = new Subject<boolean>();
  redirectIfCancel$: Subject<boolean> = new Subject<boolean>();

  isTaxLoading$: Observable<boolean>;
  estimatedTax$: Observable<EstimatedTax>;

  formGroup: FormGroup;
  fileTypes = [FileTypeBrowser.DOC, FileTypeBrowser.DOCX, FileTypeBrowser.PDF, FileTypeBrowser.RTF, FileTypeBrowser.TXT];
  acceptFileTypes = this.fileTypes.toString();

  private addRetainerAgreementSubject$: Subject<FormData> = new Subject<FormData>();
  private removeRetainerAgreementSubject$: Subject<boolean> = new Subject<boolean>();
  private feeSummarySubject$: ReplaySubject<number> = new ReplaySubject<number>(1);
  private formSubmitSubject$: Subject<Email & { sendEmail: boolean }> = new Subject<Email & { sendEmail: boolean }>();
  private resendInvitationSubject$: Subject<Email> = new Subject<Email>();
  private subscribers: any = {};

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private modalService: ModalService,
    private configDataService: ConfigDataService,
    private emailTemplatesService: EmailTemplatesService,
    private emailsService: EmailsService,
    private packagesService: PackagesService,
    private notificationsService: NotificationsService,
    private taxesService: TaxesService,
    private ngbModal: NgbModal
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.notificationsService.showComponent$.next(ActivePackageComponent);
    this.feeDetails$ = this.configDataService.feeDetails$;
    this.package$ = this.packagesService.package$;
    this.emailPostRequest$ = this.packagesService.getEmailPostRequest$;
    this.emailPutRequest$ = this.emailsService.getEmailPutRequest$;
    this.isTaxLoading$ = this.package$.pipe(
      switchMap(({ id: packageId }): Observable<boolean> =>
        this.taxesService.postEstimatedTax({type: TaxTypes.IMMIGRATION_BENEFIT, packageId}).pipe(
          map((res: RequestState<FeeSchedule>) => res.loading)
        )
      )
    );
    this.estimatedTax$ = this.taxesService.packageChangingStatusFeeWithTax$;

    this.emailType$ = this.activatedRoute.data.pipe(
      pluck('emailTemplateType')
    );
    this.showWarning$ = this.emailType$.pipe(
      map((emailType) => emailType === EmailTemplateTypes.NEW_CLIENT)
    );

    this.showSendBtnDescription$ = this.emailType$.pipe(
      map((emailType) => emailType === EmailTemplateTypes.INVITE_APPLICANT)
    );

    this.showResendBtn$ = combineLatest([
      this.emailType$,
      this.package$,
    ]).pipe(
      filter(([emailType, currentPackage]) => !!currentPackage),
      map(([emailType, currentPackage]) =>
        emailType === EmailTemplateTypes.INVITE_APPLICANT
        && currentPackage.status !== PackageStatus.LEAD
        && !!currentPackage.inviteApplicantEmailId
      )
    );

    this.title$ = this.emailType$.pipe(
      map((data) => {
        switch (data) {
          case EmailTemplateTypes.NEW_CLIENT: {
            return 'TEMPLATE.TASK_QUEUE.PACKAGE_EMAIL_TEMPLATE.NEW_CLIENT_EMAIL_TITLE';
          }
          case EmailTemplateTypes.UPDATED_CLIENT: {
            return 'TEMPLATE.TASK_QUEUE.PACKAGE_EMAIL_TEMPLATE.UPDATED_PACKAGE_EMAIL_TITLE';
          }
          case EmailTemplateTypes.INVITE_APPLICANT: {
            return 'TEMPLATE.TASK_QUEUE.PACKAGE_EMAIL_TEMPLATE.INVITATION_TO_REGISTER_EMAIL_TITLE';
          }
          default: {
            return '';
          }
        }
      })
    );

    this.file$ = combineLatest([
      this.activatedRoute.params,
      this.addRetainerAgreementSubject$,
    ]).pipe(
      switchMap(([params, data]) => this.packagesService.addRetainerAgreement(params.id, data).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
              this.modalService.showErrorModal(error.error.errors || [error.error]);
            }
            return EMPTY;
          }
        ),
        map((fileInfo) => ({id: params.id, retainerAgreement: fileInfo}))
      )),
    );

  }

  addSubscribers() {

    this.subscribers.addRetainerAgreementSubscription = this.file$.pipe(
    ).subscribe((res) => {
      this.packagesService.updatePackageInState(res);
    });

    this.subscribers.packageSubscription = this.package$.pipe(
      filter((value) => !!value),
    )
      .subscribe((currentPackage) => {
        this.formGroup.get('packageId').patchValue(currentPackage.id);
        this.formGroup.get('representativeId').patchValue(currentPackage.representativeId);
        let feeSummary = null;
        currentPackage.applicants.forEach((applicant) => feeSummary += applicant.fee ? applicant.fee : 0);
        this.feeSummarySubject$.next(feeSummary);
      });

    this.subscribers.emailTemplatesSubscription = combineLatest([
      this.emailType$,
      this.package$,
    ]).pipe(
      take(1),
      switchMap(([emailType, currentPackage]: [EmailTemplateTypes, Package]) => {
          if (currentPackage.status === PackageStatus.LEAD ||
            emailType !== EmailTemplateTypes.INVITE_APPLICANT || !currentPackage.inviteApplicantEmailId) {
            const params = {
              templateType: emailType,
              packageId: currentPackage.id,
              representativeId: currentPackage.representativeId,
            };
            return this.emailTemplatesService.getEmailTemplate(params);
          } else {
            return this.emailsService.getEmailById(currentPackage.inviteApplicantEmailId);
          }
        }
      ),
      filter((emailTemplate) => !!emailTemplate),
    ).subscribe((emailTemplate) => this.formGroup.patchValue(emailTemplate));

    this.subscribers.formSubmitSubscription = combineLatest([
      this.formSubmitSubject$,
      this.emailType$,
    ]).pipe(
      switchMap(([data, emailType]) => {
        data.sendEmail = emailType !== EmailTemplateTypes.INVITE_APPLICANT;
        return this.packagesService.sendPackageEmail(data).pipe(
          catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.modalService.showErrorModal(error.error.errors || [error.error]);
              }
              return EMPTY;
            }
          )
        );
      }),
      switchMap((res: any) => {
        if (res.templateType === EmailTemplateTypes.INVITE_APPLICANT) {
          return this.packagesService.updatePackageStatus({id: res.packageId, newStatus: PackageStatus.OPEN}).pipe(
            catchError(() => of(true))
          );
        } else {
          return of(true);
        }
      })
    ).subscribe(() => this.router.navigate(['task-queue', 'clients']));

    this.subscribers.redirectSubscription = this.redirectIfCancel$.pipe(
      switchMap(() => this.emailType$),
      withLatestFrom(this.package$.pipe(
        filter((currentPackage) => !!currentPackage),
        map((currentPackage) => currentPackage.id
        )
      ))
    ).subscribe(([emailType, packageId]) => {
      if (emailType === EmailTemplateTypes.INVITE_APPLICANT) {
        this.router.navigate(['task-queue', 'clients']);
      } else {
        this.router.navigate(['task-queue', 'package', packageId, 'edit']);
      }
    });

    this.subscribers.removeAgreementSubscription = combineLatest([
      this.activatedRoute.params,
      this.removeRetainerAgreementSubject$
    ]).pipe(
      switchMap(([params]) => this.packagesService.removeRetainerAgreement(params.id)
        .pipe(
          catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.modalService.showErrorModal(error.error.errors || [error.error]);
              }
              return EMPTY;
            }
          ),
          map(() => ({id: params.id, retainerAgreement: null}))
        )
      ),
    ).subscribe((res) => {
      this.packagesService.updatePackageInState(res);
    });

    this.subscribers.resendInvitationSunscription = combineLatest([
      this.resendInvitationSubject$,
      this.package$,
    ]).pipe(
      switchMap(([email, currentPackage]: [Email, Package]) => {
        const data = {
          content: email.content,
          subject: email.subject,
          id: currentPackage.inviteApplicantEmailId,
          sendEmail: true,
        };
        return this.emailsService.updateEmail(data).pipe(
          catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.modalService.showErrorModal(error.error.errors || [error.error]);
              }
              return EMPTY;
            }
          )
        );
      }),
      switchMap((data: any) => {
        this.resendSuccessModal(data);
        return of(true);
      })
    ).subscribe(() => {
      this.router.navigate(['task-queue', 'clients']);
    });

  }

  resendSuccessModal(data: any) {
    return this.modalService.openConfirmModal({
      header: 'FORM.BUTTON.SEND_EMAIL',
      body: data.responseMessage,
      centered: true,
      buttons: [OkButtonLg]
    }).pipe(
      catchError(() => EMPTY),
    );
  }

  ngOnDestroy() {
    this.notificationsService.showComponent$.next(null);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      templateType: new FormControl(),
      content: new FormControl('', [Validators.required, noWhitespaceValidator]),
      packageId: new FormControl(),
      representativeId: new FormControl(),
      subject: new FormControl('', [Validators.required, noWhitespaceValidator]),
      sendEmail: new FormControl(false),
    });
  }

  sendEmail() {
    this.formSubmitSubject$.next(this.formGroup.value);
  }

  previewEmail(body) {
    this.modalService.openConfirmModal(
      {
        header: 'TEMPLATE.MODAL.PREVIEW_EMAIL_MODAL',
        buttons: [OkButton],
        windowClass: 'email-preview-modal',
        body,
      }
    );
  }

  addRetainerAgreement(event) {
    const file: File = event.target.files[0];
    const formData: FormData = new FormData();
    const isFileAllowed = checkAllowedFileType(file.type, this.fileTypes);
    this.isFileNotAllowed$.next(!isFileAllowed);
    const isFileSizeAllowed = validateFileSize(file.size);
    this.isFileSizeNotAllowed$.next(!isFileSizeAllowed);
    if (file && isFileAllowed && isFileSizeAllowed) {
      formData.append('retainerFile', file);
      this.addRetainerAgreementSubject$.next(formData);
    }
  }

  removeRetainerAgreement() {
    this.removeRetainerAgreementSubject$.next(true);
  }

  redirectIfCancel() {
    this.redirectIfCancel$.next(true);
  }

  openEmailTemplateVariablesModal(emailType) {
    const modalRef = this.ngbModal.open(EmailTemplateVariablesModalComponent, {
      centered: true
    });
    modalRef.componentInstance.emailTemplateType = emailType;
  }

  resendInvitation() {
    this.resendInvitationSubject$.next(this.formGroup.value);
  }
}

