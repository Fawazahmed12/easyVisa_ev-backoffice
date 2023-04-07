import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, filter, map, pluck, switchMap, take, withLatestFrom } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ModalService, OrganizationService, PackagesService, UserService } from '../../../../../core/services';
import { User } from '../../../../../core/models/user.model';
import { Role } from '../../../../../core/models/role.enum';
import { Organization } from '../../../../../core/models/organization.model';
import { OrganizationType } from '../../../../../core/models/organization-type.enum';
import { Profile } from '../../../../../core/models/profile.model';
import { RequestState } from '../../../../../core/ngrx/utils';
import { NoPackageSelectModalComponent } from '../../../../../core/modals/no-package-select-modal/no-package-select-modal.component';
import { ApplicantType } from '../../../../../core/models/applicantType.enum';

import { EditPreviewProfileService } from '../../edit-preview-profile.service';
import { OrganizationProfile } from '../../models/organization-profile.model';

import { ProfileEditService } from './profile-edit.service';


@Component({
  selector: 'app-profile-edit',
  templateUrl: './profile-edit.component.html',
  styleUrls: [ './profile-edit.component.scss' ],
  providers: [ ProfileEditService ],
})

@DestroySubscribers()
export class ProfileEditComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() isOrganizationProfile = false;
  @ViewChild('profilePreview', { static: true }) profilePreview;

  title$: Observable<string>;
  applicantId$: Observable<number>;
  organization$: Observable<OrganizationProfile>;
  organizationPutRequest$: Observable<RequestState<OrganizationProfile>>;
  previewButtonLabel$: Observable<string>;
  profile$: Observable<Profile>;
  profilePutRequest$: Observable<RequestState<Profile>>;
  cancelModificationsSubject$: Subject<boolean> = new Subject();
  updateProfileSubject$: Subject<any> = new Subject();
  activeOrganization$: Observable<Organization>;
  previewModalHeader$: Observable<string>;
  isAttorney$: Observable<boolean>;
  isEmployee$: Observable<boolean>;
  isUser$: Observable<boolean>;
  isEmployeeAdmin$: Observable<boolean>;
  activePackageId$: Observable<number>;
  redirectApplicantSubject$: Subject<{ petitioner: string; beneficiary: string }> = new Subject();

  previewModalHeader: string;
  Role = Role;

  private subscribers: any = {};

  constructor(
    private organizationService: OrganizationService,
    private modalService: ModalService,
    private profileService: EditPreviewProfileService,
    private packagesService: PackagesService,
    private profileEditService: ProfileEditService,
    private userService: UserService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private ngbModal: NgbModal,
  ) {
  }

  get profileFormGroup() {
    return this.profileEditService.profileFormGroup;
  }

  get addressFormGroup() {
    return this.profileFormGroup.get('officeAddress');
  }

  get firstFormControl() {
    return this.profileFormGroup.get('firstName');
  }

  get middleFormControl() {
    return this.profileFormGroup.get('middleName');
  }

  get lastFormControl() {
    return this.profileFormGroup.get('lastName');
  }

  get labelForId() {
    return !this.isOrganizationProfile ? 'FORM.LABELS.EV_ID' : 'TEMPLATE.ACCOUNT.PROFILE.LAW_FIRM_ID';
  }

  get profileOrgOrRep$() {
    return !this.isOrganizationProfile ? this.profile$ : this.organization$;
  }

  ngOnInit() {
    this.activePackageId$ = this.packagesService.activePackageId$;
    this.profile$ = this.profileService.profile$;
    this.organization$ = this.profileService.organization$;
    this.profilePutRequest$ = this.profileService.profilePutRequest$;
    this.organizationPutRequest$ = this.profileService.organizationPutRequest$;
    this.isAttorney$ = this.userService.hasAccess([ Role.ROLE_ATTORNEY ]);
    this.isEmployee$ = this.userService.hasAccess([ Role.ROLE_EMPLOYEE ]);
    this.isUser$ = this.userService.hasAccess([ Role.ROLE_USER ]);
    this.activeOrganization$ = this.organizationService.activeOrganization$;

    this.previewModalHeader$ = this.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      map((activeOrganization) => this.profileService.getProfilePreviewTitle(this.isOrganizationProfile, activeOrganization.organizationType))
    );

    this.isEmployeeAdmin$ = this.userService.hasAccess([ Role.ROLE_EMPLOYEE ]).pipe(
      withLatestFrom(this.organizationService.activeOrganization$),
      map(([ isEmployee, activeOrganization ]) => isEmployee && activeOrganization.isAdmin)
    );

    this.title$ = combineLatest([
      this.userService.currentUser$,
      this.organizationService.activeOrganization$
    ]).pipe(
      filter(([ user, organization ]) => !!user && !!organization),
      map(([ user, organization ]: [ User, Organization ]) => {
        if (!this.isOrganizationProfile) {
          if (user.roles.some((userRole: Role) => userRole === Role.ROLE_EMPLOYEE)) {
            return 'TEMPLATE.ACCOUNT.PROFILE.TITLE_EMPLOYEE_EDIT';
          } else {
            switch (organization.organizationType) {
              case OrganizationType.SOLO_PRACTICE: {
                return 'TEMPLATE.ACCOUNT.PROFILE.TITLE_SOLO_PRACTITIONER_EDIT';
              }
              case OrganizationType.LAW_FIRM: {
                return 'TEMPLATE.ACCOUNT.PROFILE.TITLE_MEMBER_OF_LAW_PRACTICE_EDIT';
              }
              case OrganizationType.RECOGNIZED_ORGANIZATION: {
                return 'TEMPLATE.ACCOUNT.PROFILE.TITLE_ACCREDITED_REPRESENTATIVE_EDIT';
              }
              default: {
                return '';
              }
            }
          }
        } else {
          switch (organization.organizationType) {
            case OrganizationType.LAW_FIRM: {
              return 'TEMPLATE.ACCOUNT.PROFILE.TITLE_LAW_FIRM_EDIT';
            }
            case OrganizationType.RECOGNIZED_ORGANIZATION: {
              return 'TEMPLATE.ACCOUNT.PROFILE.TITLE_RECOGNIZE_ORGANIZATION_EDIT';
            }
            default: {
              return '';
            }
          }
        }
      })
    );

    this.previewButtonLabel$ = this.organizationService.activeOrganization$.pipe(
      filter((organization) => !!organization),
      map((organization: Organization) => {
        if (!this.isOrganizationProfile) {
          switch (organization.organizationType) {
            case OrganizationType.SOLO_PRACTICE:
            case OrganizationType.LAW_FIRM: {
              return 'TEMPLATE.ACCOUNT.PROFILE.PREVIEW_ATTORNEY_PROFILE';
            }
            case OrganizationType.RECOGNIZED_ORGANIZATION: {
              return 'TEMPLATE.ACCOUNT.PROFILE.PREVIEW_REPRESENTATIVE_PROFILE';
            }
            default: {
              return '';
            }
          }
        } else {
          switch (organization.organizationType) {
            case OrganizationType.LAW_FIRM: {
              return 'TEMPLATE.ACCOUNT.PROFILE.PREVIEW_LAW_FIRM_PROFILE';
            }
            case OrganizationType.RECOGNIZED_ORGANIZATION: {
              return 'TEMPLATE.ACCOUNT.PROFILE.PREVIEW_ORGANIZATION_PROFILE';
            }
            default: {
              return '';
            }
          }
        }
      })
    );

    this.applicantId$ = this.profileService.profile$.pipe(
      filter((profile) => !!profile),
      pluck('id'),
    );
  }

  addSubscribers() {
    this.subscribers.previewModalHeaderSubscription = this.previewModalHeader$.pipe(
      filter((header) => !!header),
    )
      .subscribe((header) => {
        this.previewModalHeader = header;
      });

    this.subscribers.formGroupDataSubscription = this.profile$.pipe(
      filter((profile) => !!profile && !this.isOrganizationProfile),
      withLatestFrom(this.userService.currentUser$),
      withLatestFrom(this.organizationService.activeOrganization$),
    ).subscribe(([ [ profile, user ], activeOrganization ]) => {
      if (user.roles.some((userRole: Role) => userRole === Role.ROLE_ATTORNEY)) {
        this.profileEditService.createAttorneyProfileFormGroup(profile);
      } else if (user.roles.some((userRole: Role) => userRole === Role.ROLE_EMPLOYEE)) {
        activeOrganization && activeOrganization.isAdmin ?
          this.profileEditService.createEmployeeAdminProfileFormGroup(profile) :
          this.profileEditService.createEmployeeNonAdminFormGroup(profile);
      } else if (user.roles.some((userRole: Role) => userRole === Role.ROLE_USER)) {
        this.profileEditService.createUserFormGroup(profile);
        this.profileFormGroup.disable();
      }
    });

    this.subscribers.organizationSubscription = this.organization$.pipe(
      filter((organization) => !!organization && this.isOrganizationProfile)
    ).subscribe((organization) => this.profileEditService.createLawFirmFormGroup(organization));

    this.subscribers.cancelModificationsSubscription = this.cancelModificationsSubject$.pipe(
      withLatestFrom(this.profile$),
      withLatestFrom(this.organization$),
    ).subscribe(([ [ , profile ], organization ]) => {
      this.profileEditService.resetProfileFormGroup(this.isOrganizationProfile ? organization : profile);
    });

    this.subscribers.updateProfileSubscription = this.updateProfileSubject$.pipe(
      filter(() => this.profileFormGroup.valid),
      switchMap((profileData) => {
          if (!this.isOrganizationProfile) {
            return this.profileService.updateProfile(profileData).pipe(
              this.showErrorModalWithResponse(),
            );
          } else {
            return this.profileService.updateOrganization(profileData).pipe(
              this.showErrorModalWithResponse(),
            );
          }
        }
      )
    ).subscribe();

    this.subscribers.actireRouterSubscription = this.activatedRoute.queryParams.pipe(
      filter(params => !!params && !!params.setActiveSolo),
      withLatestFrom(this.organizationService.organizations$),
      map(([ , organizations ]) =>
        organizations.find((org) => org.organizationType === OrganizationType.SOLO_PRACTICE).id
      ),
      take(1),
    ).subscribe((soloId) => {
      this.router.navigate([]).catch((err) => console.log(err));
      this.organizationService.organizationIdControl.patchValue(soloId);
    });

    this.subscribers.redirectApplicantSubscription = this.redirectApplicantSubject$.pipe(
      withLatestFrom(
        this.packagesService.activePackage$,
        this.applicantId$,
      ),
    ).subscribe(([ url, activePackage, applicantId ]) => {
      const isPetitioner = activePackage.applicants.find(
        applicant => applicant.profile.id === applicantId
      ).applicantType === ApplicantType.PETITIONER;
      const urlResult = isPetitioner ? url.petitioner : url.beneficiary;

      if (!!activePackage.id) {
        this.router.navigate([ '/', 'questionnaire', 'package', activePackage.id, 'applicants', applicantId, 'sections', urlResult ]);
      } else {
        this.openPackageWarningModal();
      }
    });
  }

  ngOnDestroy() {
    this.profileEditService.resetFormGroup();
  }

  cancelModifications() {
    this.cancelModificationsSubject$.next(true);
  }

  updateProfile() {
    this.updateProfileSubject$.next(this.profileFormGroup.value);
  }

  showErrorModalWithResponse() {
    return (observable) => observable.pipe(
      catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [ error.error ]);
          }
          return EMPTY;
        }
      ),
    );
  }

  redirectApplicant(data) {
    const url = {
      contact: { petitioner: 'Sec_contactInformation', beneficiary: 'Sec_contactInformationForBeneficiary' },
      name: { petitioner: 'Sec_2', beneficiary: 'Sec_nameForBeneficiary' },
      address: { petitioner: 'Sec_addressHistory', beneficiary: 'Sec_addressHistoryForBeneficiary' },
    };
    this.redirectApplicantSubject$.next(url[ data ]);
  }

  private openPackageWarningModal() {
    this.ngbModal.open(NoPackageSelectModalComponent, { centered: true });
    return of(true);
  }
}
