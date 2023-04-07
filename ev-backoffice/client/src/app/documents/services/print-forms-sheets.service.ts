import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';
import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';

import {
  GetBlanks,
  GetDownloadBlanks,
  GetDownloadForms,
  GetFormsSheets,
  GetPrintBlank,
  GetPrintForm,
  SelectApplicants
} from '../ngrx/forms-sheets/forms-sheets.actions';
import {
  BlankForm,
  FormsPackageApplicant,
  FormsSheets,
  PackageContinuationSheets,
  PackageForm
} from '../models/forms-sheets.model';
import {
  getBlanks,
  getBlanksGetRequestState,
  getCurrentContinuationSheets,
  getCurrentPackageForms,
  getDownloadBlanksGetRequestState,
  getDownloadFormsGetRequestState,
  getFormsSheetsGetRequestState,
  getPackageApplicants,
  getPrintBlankGetRequestState,
  getPrintFormGetRequestState,
  getSelectedApplicants,
  getSelectedApplicantsIds
} from '../ngrx/state';

@Injectable()
export class PrintFormsSheetsService {
  getFormsSheetsRequestState$: Observable<RequestState<FormsSheets>>;
  printFormGetRequestState$: Observable<RequestState<any>>;
  packageApplicants$: Observable<FormsPackageApplicant[]>;
  selectedApplicants$: Observable<FormsPackageApplicant[]>;
  selectedApplicantsIds$: Observable<number[]>;
  currentPackageForms$: Observable<PackageForm[]>;
  currentContinuationSheets$: Observable<PackageContinuationSheets[]>;
  downloadFormsGetRequestState$: Observable<RequestState<any>>;
  blanksGetRequestState$: Observable<RequestState<BlankForm[]>>;
  downloadBlanksGetRequestState$: Observable<RequestState<any>>;
  printBlankGetRequestState$: Observable<RequestState<any>>;
  blanks$: Observable<BlankForm[]>;


  constructor(
    private store: Store<State>,
  ) {
    this.getFormsSheetsRequestState$ = this.store.pipe(select(getFormsSheetsGetRequestState));
    this.packageApplicants$ = this.store.pipe(select(getPackageApplicants));
    this.selectedApplicants$ = this.store.pipe(select(getSelectedApplicants));
    this.selectedApplicantsIds$ = this.store.pipe(select(getSelectedApplicantsIds));
    this.currentPackageForms$ = this.store.pipe(select(getCurrentPackageForms));
    this.currentContinuationSheets$ = this.store.pipe(select(getCurrentContinuationSheets));
    this.printFormGetRequestState$ = this.store.pipe(select(getPrintFormGetRequestState));
    this.downloadFormsGetRequestState$ = this.store.pipe(select(getDownloadFormsGetRequestState));
    this.blanksGetRequestState$ = this.store.pipe(select(getBlanksGetRequestState));
    this.downloadBlanksGetRequestState$ = this.store.pipe(select(getDownloadBlanksGetRequestState));
    this.printBlankGetRequestState$ = this.store.pipe(select(getPrintBlankGetRequestState));
    this.blanks$ = this.store.pipe(select(getBlanks));
  }

  getFormsSheets(data) {
    this.store.dispatch(new GetFormsSheets(data));
    return this.getFormsSheetsRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  setCurrentApplicants(data) {
    this.store.dispatch(new SelectApplicants(data));
  }

  printUSCISForm(data) {
    this.store.dispatch(new GetPrintForm(data));
    return this.printFormGetRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  downloadForms(data) {
    this.store.dispatch(new GetDownloadForms(data));
    return this.downloadFormsGetRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  getBlankForms(data) {
    this.store.dispatch(new GetBlanks(data));
    return this.blanksGetRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  downloadBlanksForm(data) {
    this.store.dispatch(new GetDownloadBlanks(data));
    return this.downloadBlanksGetRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  printBlankForm(data) {
    this.store.dispatch(new GetPrintBlank(data));
    return this.printBlankGetRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }
}
