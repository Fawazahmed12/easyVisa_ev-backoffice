import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { catchError, filter, map, pluck, startWith, switchMap, take } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import { Subject } from 'rxjs';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';
import { AuthService, ModalService, UserService } from '../../core/services';
import { RepresentativeType } from '../../core/models/representativeType.enum';
import { AttorneyType } from '../../core/models/attorney-type.enum';
import { Attorney } from '../../core/models/attorney.model';
import { SignUpService } from '../services';


@Component({
  selector: 'app-select-representative-type',
  templateUrl: './select-representative-type.component.html',
})
@DestroySubscribers()
export class SelectRepresentativeTypeComponent implements OnInit, OnDestroy, AddSubscribers {

  private profileNotLinkedModalSubject$: Subject<boolean> = new Subject<boolean>();
  private selectRepTypeSubject$: Subject<any> = new Subject<any>();

  @ViewChild('profileNotLinked', { static: true }) public profileNotLinked;

  form: FormGroup;

  options = [
    {
      label: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.ATTORNEY',
      value: RepresentativeType.ATTORNEY,
      isDisabled: null
    },
    {
      label: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.MEMBER_ORG',
      value: RepresentativeType.ACCREDITED_REPRESENTATIVE,
      isDisabled: true
    },
  ];

  attorneyTypes = [
    {
      label: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SOLO_PRACTITIONER',
      value: AttorneyType.SOLO_PRACTITIONER,
    },
    {
      label: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.MEMBER_LAW_FIRM',
      value: AttorneyType.MEMBER_OF_A_LAW_FIRM,
    },
  ];

  controlsList = [
    {
      label: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SELECT_STATE',
      placeholder: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SELECT_STATE_PLACEHOLDER',
      options: [
        'first',
      ],
      controlName: 'state',
    },
    {
      label: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SELECT_CITY',
      placeholder: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SELECT_CITY_PLACEHOLDER',
      options: [
        'first',
      ],
      controlName: 'city',
    },
    {
      label: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SELECT_ORG',
      placeholder: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SELECT_ORG_PLACEHOLDER',
      options: [
        'first',
      ],
      controlName: 'org',
    },
    {
      label: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SELECT_NAME',
      placeholder: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.SELECT_NAME_PLACEHOLDER',
      options: [
        'first',
      ],
      controlName: 'name',
    },
  ];

  private subscribers: any = {};

  constructor(
    private modalService: ModalService,
    private authService: AuthService,
    private userService: UserService,
    private signUpService: SignUpService,
    private router: Router,
  ) {
  }

  get typeControl() {
    return this.form.get('representativeType');
  }

  get attorneyTypeControl() {
    return this.form.get('attorneyType');
  }

  get type$() {
    return this.typeControl.valueChanges
    .pipe(
      startWith(this.typeControl.value),
    );
  }

  get attorneyType$() {
    return this.attorneyTypeControl.valueChanges
    .pipe(
      startWith(this.attorneyTypeControl.value),
    );
  }

  get showLinkForm$() {
    return this.type$.pipe(
      map((type) => type && type === RepresentativeType.ACCREDITED_REPRESENTATIVE),
    );
  }

  get showSelectAttorney$() {
    return this.type$.pipe(
      map((type) => type && type === RepresentativeType.ATTORNEY),
    );
  }

  get showMemberFirmDescription$() {
    return this.attorneyType$.pipe(
      map((attorneyType) => attorneyType && attorneyType === AttorneyType.MEMBER_OF_A_LAW_FIRM),
    );
  }

  ngOnInit() {
    const representativeTypeControl = new FormControl('', Validators.required);
    const attorneyTypeControl = new FormControl('', Validators.required);
    const stateControl = new FormControl('', Validators.required);
    const cityControl = new FormControl('', Validators.required);
    const orgControl = new FormControl('', Validators.required);
    const nameControl = new FormControl('', Validators.required);

    stateControl.disable({emitEvent: false});
    attorneyTypeControl.disable({emitEvent: false});
    cityControl.disable({emitEvent: false});
    orgControl.disable({emitEvent: false});
    nameControl.disable({emitEvent: false});

    this.form = new FormGroup({
      id: new FormControl(''),
      representativeType: representativeTypeControl,
      attorneyType: attorneyTypeControl,
      state: stateControl,
      city: cityControl,
      org: orgControl,
      name: nameControl,
    });
  }

  addSubscribers() {
    this.subscribers.selectRepTypeSubscription = this.selectRepTypeSubject$.pipe(
      filter((form) => form.valid),
      switchMap((form) => {
        this.signUpService.setRegistrationRepresentativeType(
          form.value.attorneyType ? form.value.attorneyType : form.value.representativeType
        );
        return this.signUpService.updateRepresentativeType(form.value).pipe(
          catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
              this.modalService.showErrorModal(error.error.errors || [error.error]);
            }
            return EMPTY;
          }),
        );
      }),
    ).subscribe(() => this.router.navigate(['auth', 'attorney-welcome']));

    this.subscribers.profileSubscription = this.userService.currentUser$.pipe(
      take(1),
      pluck('profile'),
    )
    .subscribe((profile: Attorney) => this.form.patchValue({
      id: profile.id,
    }));

    this.subscribers.typeSubsribtion = this.type$.subscribe(
      (type) => {
        switch (type) {
          case RepresentativeType.ATTORNEY:
            this.form.get('state').disable();
            this.form.get('city').disable();
            this.form.get('org').disable();
            this.form.get('name').disable();
            this.attorneyTypeControl.enable();
            return true;
          case RepresentativeType.ACCREDITED_REPRESENTATIVE:
            this.form.get('state').enable();
            this.form.get('city').enable();
            this.form.get('org').enable();
            this.form.get('name').enable();
            this.attorneyTypeControl.disable();
            return true;
          default:
            this.form.get('state').disable();
            this.form.get('city').disable();
            this.form.get('org').disable();
            this.form.get('name').disable();
            this.attorneyTypeControl.disable();
            return true;
        }
      });
    this.subscribers.notLinkedModalSubscription = this.profileNotLinkedModalSubject$.pipe(
      switchMap(() => {
        const buttons = [
          {
            label: 'FORM.BUTTON.CANCEL',
            type: ConfirmButtonType.Dismiss,
            className: 'btn btn-primary mr-2 min-w-100',
          },
          {
            label: 'FORM.BUTTON.LOG_OUT',
            type: ConfirmButtonType.Close,
            className: 'btn btn-primary mr-2 min-w-100',
          },
        ];
        return this.modalService.openConfirmModal({
          header: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.PROFILE_NOT_LINKED',
          body: this.profileNotLinked,
          buttons,
          size: 'lg',
          centered: true,
          showCloseIcon: true,
        }).pipe(
          catchError(() => EMPTY),
        );
      })
    )
    .subscribe(() => this.authService.logout());
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  formSubmit() {
    if (this.typeControl.value === RepresentativeType.ACCREDITED_REPRESENTATIVE && this.form.invalid) {
      this.openProfileNotLinkedModal();
    }
    if (!this.typeControl.value || this.typeControl.value === RepresentativeType.ATTORNEY && this.form.invalid) {
      this.openRepTypeNotSelectedModal();
    }
    this.selectRepTypeSubject$.next(this.form);
  }

  openRepTypeNotSelectedModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    this.modalService.openConfirmModal({
      header: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.REPRESENTATIVE_TYPE_NOT_SELECTED',
      body: 'TEMPLATE.AUTH.SELECT_REPRESENTATIVE_TYPE.REPRESENTATIVE_TYPE_NOT_SELECTED_TEXT',
      buttons,
      showCloseIcon: true,
      centered: true,
    });
  }

  openProfileNotLinkedModal() {
    this.profileNotLinkedModalSubject$.next(true);
  }

}
