import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { fromPromise } from 'rxjs/internal-compatibility';
import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, switchMap } from 'rxjs/operators';

import { reject } from 'lodash-es';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { BeneficiaryCountriesModalComponent } from '../../beneficiary-countries-modal/beneficiary-countries-modal.component';

import { PackageStatus } from '../../../../../../core/models/package/package-status.enum';
import { states } from '../../../../../../core/models/states';
import { makeArrayFromParam } from '../../../../../../shared/utils/make-array-from-param';
import { countries } from '../../../../../../core/models/countries';
import { DATE_PATTERN } from '../../../../../../shared/validators/constants/date-pattern.const';
import { startEndDateValidator } from '../../../../../../shared/validators/start-end-date.validator';
import { CitizenshipStatus } from '../../../../../../core/models/citizenship-status.enum';
import { BenefitCategoryModel } from '../../../../../../core/models/benefits.model';
import { ConfigDataService } from '../../../../../../core/services';

@Component({
  selector: 'app-multiple-client-search',
  templateUrl: 'multiple-client-search.component.html',
  styleUrls: ['multiple-client-search.component.scss']
})
@DestroySubscribers()
export class MultipleClientSearchComponent implements OnInit, OnDestroy, AddSubscribers {
  selectCountriesSubject$: Subject<boolean> = new Subject();
  allBenefitCategories$: Observable<BenefitCategoryModel[]>;
  selectBenefitCheckboxesSubject$: Subject<any> = new Subject<any>();

  selectedFiltersFormGroup = new FormGroup({
      benefitCategory: new FormControl(null),
      countries: new FormControl(null),
      closedDateStart: new FormControl(null, Validators.pattern(DATE_PATTERN)),
      closedDateEnd: new FormControl(null, Validators.pattern(DATE_PATTERN)),
      openedDateStart: new FormControl(null, Validators.pattern(DATE_PATTERN)),
      openedDateEnd: new FormControl(null, Validators.pattern(DATE_PATTERN)),
      lastAnsweredOnDateStart: new FormControl(null, Validators.pattern(DATE_PATTERN)),
      lastAnsweredOnDateEnd: new FormControl(null, Validators.pattern(DATE_PATTERN)),
      isOwed: new FormControl(false),
      petitionerStatus: new FormControl(null),
      states: new FormControl(null),
      status: new FormControl(null),
    }, {
      validators: [
        startEndDateValidator('closedDateStart', 'closedDateEnd'),
        startEndDateValidator('openedDateStart', 'openedDateEnd'),
        startEndDateValidator('lastAnsweredOnDateStart', 'lastAnsweredOnDateEnd'),
      ],
    }
  );

  statuses = [
    {label: 'TEMPLATE.TASK_QUEUE.CLIENTS.OPEN', value: PackageStatus.OPEN, checked: false},
    {label: 'TEMPLATE.TASK_QUEUE.CLIENTS.LEAD', value: PackageStatus.LEAD, checked: false},
    {label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLOSED', value: PackageStatus.CLOSED, checked: false},
    {label: 'TEMPLATE.TASK_QUEUE.CLIENTS.BLOCKED', value: PackageStatus.BLOCKED, checked: false},
    {label: 'TEMPLATE.TASK_QUEUE.CLIENTS.TRANSFERRED', value: PackageStatus.TRANSFERRED, checked: false},
  ];

  states = states;

  petitionerStatuses = [
    {label: 'TEMPLATE.TASK_QUEUE.APPLICANT.US_CITIZEN', value: CitizenshipStatus.U_S_CITIZEN, checked: false},
    {label: 'TEMPLATE.TASK_QUEUE.APPLICANT.LPR_SHOT', value: CitizenshipStatus.LPR, checked: false},
    {label: 'TEMPLATE.TASK_QUEUE.APPLICANT.ALIEN', value: CitizenshipStatus.ALIEN, checked: false},
    {label: 'TEMPLATE.TASK_QUEUE.APPLICANT.US_NATIONAL', value: CitizenshipStatus.U_S_NATIONAL, checked: false},

  ];

  controlChips = [
    {control: this.IsOwedFormControl, label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLIENT_SEARCH_MODAL.MONEY_OWED', isDate: false},
    {control: this.closedDateStartFormControl, label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLIENT_SEARCH_MODAL.CLOSED_FROM', isDate: true},
    {control: this.closedDateEndFormControl, label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLIENT_SEARCH_MODAL.CLOSED_TO', isDate: true},
    {control: this.openedDateStartFormControl, label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLIENT_SEARCH_MODAL.OPENED_FROM', isDate: true},
    {control: this.openedDateEndFormControl, label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLIENT_SEARCH_MODAL.OPENED_TO', isDate: true},
    {
      control: this.lastAnsweredOnDateStartFormControl,
      label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLIENT_SEARCH_MODAL.INACTIVE_FROM',
      isDate: true
    },
    {control: this.lastAnsweredOnDateEndFormControl, label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLIENT_SEARCH_MODAL.INACTIVE_TO', isDate: true},
  ];

  beneficiaryCountries = [...countries];

  isFormIntiallyEmpty: boolean;

  private subscribers: any = {};

  constructor(
    private ngbModal: NgbModal,
    private activeModal: NgbActiveModal,
    private route: ActivatedRoute,
    private configDataService: ConfigDataService,
  ) {
  }

  get benefitFormControl() {
    return this.selectedFiltersFormGroup.get('benefitCategory') as FormControl;
  }

  get benefitFormControlValue() {
    return this.selectedFiltersFormGroup.get('benefitCategory').value;
  }

  get statusFormControlValue() {
    return this.selectedFiltersFormGroup.get('status').value;
  }

  get countriesFormControlValue() {
    return this.selectedFiltersFormGroup.get('countries').value;
  }

  get IsOwedFormControl() {
    return this.selectedFiltersFormGroup.get('isOwed');
  }

  get statesFormControlValue() {
    return this.selectedFiltersFormGroup.get('states').value;
  }

  get petitionerStatusFormControlValue() {
    return this.selectedFiltersFormGroup.get('petitionerStatus').value;
  }

  get closedDateStartFormControl() {
    return this.selectedFiltersFormGroup.get('closedDateStart');
  }

  get closedDateEndFormControl() {
    return this.selectedFiltersFormGroup.get('closedDateEnd');
  }

  get openedDateStartFormControl() {
    return this.selectedFiltersFormGroup.get('openedDateStart');
  }

  get openedDateEndFormControl() {
    return this.selectedFiltersFormGroup.get('openedDateEnd');
  }

  get lastAnsweredOnDateStartFormControl() {
    return this.selectedFiltersFormGroup.get('lastAnsweredOnDateStart');
  }

  get lastAnsweredOnDateEndFormControl() {
    return this.selectedFiltersFormGroup.get('lastAnsweredOnDateEnd');
  }

  get filteredControlChips() {
    return this.controlChips.filter((control) => control.control.value);
  }

  get isFormEmpty() {
    return !Object.values(this.selectedFiltersFormGroup.value).filter((value) => !!value).length;
  }

  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
    this.clearAllFilters();
    this.allBenefitCategories$ = this.configDataService.allBenefitCategories$;
  }

  addSubscribers() {
    this.subscribers.selectCountrySubscription = this.selectCountriesSubject$.pipe(
      switchMap(() => this.openCountriesModal().pipe(
        catchError(() => EMPTY)
      )),
    ).subscribe((res) => {
      if (res.length) {
        const selectedCountries = res.map((country) => country.value);
        this.selectedFiltersFormGroup.patchValue({countries: selectedCountries});
      } else {
        this.selectedFiltersFormGroup.patchValue({countries: null});
      }
    });

    this.subscribers.selectCountrySubscription = this.selectBenefitCheckboxesSubject$.pipe(
    ).subscribe(({item, event}) => {
      item.checked = event.target ? event.target.checked : event;

      const initialValue = !!this.benefitFormControlValue && this.benefitFormControlValue.length ? this.benefitFormControlValue : [];
      const selectedItems = [...initialValue, item.value];
      const updatedItems = item.checked ?
        [
          ...selectedItems
        ] : [
          ...selectedItems.filter(selectedItem => selectedItem !== item.value)
        ];
      this.selectedFiltersFormGroup.patchValue({['benefitCategory']: [...updatedItems]});
    });


    this.subscribers.queryPartamsSubscription = this.route.queryParams.pipe(
      filter((params) => !!params),
    ).subscribe((params) => {
      this.selectedFiltersFormGroup.patchValue({
        benefitCategory: params.benefitCategory && makeArrayFromParam(params.benefitCategory) || null,
        countries: params.countries && makeArrayFromParam(params.countries) || null,
        closedDateStart: params.closedDateStart || null,
        closedDateEnd: params.closedDateEnd || null,
        openedDateStart: params.openedDateStart || null,
        openedDateEnd: params.openedDateEnd || null,
        lastAnsweredOnDateStart: params.lastAnsweredOnDateStart || null,
        lastAnsweredOnDateEnd: params.lastAnsweredOnDateEnd || null,
        status: params.status && makeArrayFromParam(params.status) || null,
        petitionerStatus: params.petitionerStatus && makeArrayFromParam(params.petitionerStatus) || null,
        isOwed: params.isOwed && params.isOwed === 'true',
        states: params.states && makeArrayFromParam(params.states) || null,
      });
      this.putCheckboxes(this.statusFormControlValue, this.statuses);
      this.putCheckboxes(this.statesFormControlValue, this.states);
      this.putCheckboxes(this.countriesFormControlValue, this.beneficiaryCountries);
      this.putCheckboxes(this.petitionerStatusFormControlValue, this.petitionerStatuses);
      this.isFormIntiallyEmpty = this.isFormEmpty;
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  putCheckboxes(controlValue, array) {
    if (controlValue) {
      controlValue.map((selectedItem) => {
        const foundedItem = array.find((item) => item.value === selectedItem);
        foundedItem.checked = true;
      });
    }
  }

  openCountriesModal() {
    const modalRef = this.ngbModal.open(BeneficiaryCountriesModalComponent, {
      windowClass: 'custom-modal-lg upper-modal',
      centered: true,
      backdropClass: 'upper-backdrop'
    });
    modalRef.componentInstance.countriesFormControl = this.selectedFiltersFormGroup.get('countries');
    modalRef.componentInstance.countries = this.beneficiaryCountries;
    return fromPromise(modalRef.result);
  }

  submit() {
    this.activeModal.close(this.selectedFiltersFormGroup.value);
  }

  selectCountries() {
    this.selectCountriesSubject$.next(true);
  }

  selectItem(item, items, event, controlName) {
    item.checked = event.target ? event.target.checked : event;
    const selectedItems = items.filter((currentItem) => currentItem.checked);
    if (selectedItems.length) {
      const selectedItemsValues = selectedItems.map((selectedItem) => selectedItem.value);
      this.selectedFiltersFormGroup.patchValue({[controlName]: selectedItemsValues});
    } else {
      this.selectedFiltersFormGroup.patchValue({[controlName]: null});
    }
  }

  clearAllFilters() {
    this.selectedFiltersFormGroup.reset();
    this.statuses.forEach((status) => status.checked = false);
    this.clearAllStates();
    this.petitionerStatuses.forEach((status) => status.checked = false);
  }

  clearAllStates() {
    this.states.forEach((state: any) => state.checked = false);
    this.selectedFiltersFormGroup.patchValue({states: null});
  }

  clearAllBenefitCategories() {
    this.benefitFormControl.patchValue(null);
  }

  removeSelectedItem(item, controlValue, controlName, array?) {
    const filteredValue = reject(controlValue, (currentItem) => currentItem === item);
    const value = filteredValue && filteredValue.length ? filteredValue : null;
    this.selectedFiltersFormGroup.patchValue({[controlName]: value});
    const foundedItem = array?.find((currentItem) => currentItem.value === item);
    if (foundedItem) {
      foundedItem.checked = false;
    }
  }
}
