import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, ValidationErrors, ValidatorFn } from '@angular/forms';

import { EMPTY, Observable, Subject } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { catchError, filter, switchMap, withLatestFrom } from 'rxjs/operators';

import * as FileSaver from 'file-saver';

import { PrintFormsSheetsService } from '../../services/print-forms-sheets.service';
import { BlankForm } from '../../models/forms-sheets.model';
import { RequestState } from '../../../core/ngrx/utils';
import { PackagesService } from '../../../core/services';


@Component({
  selector: 'app-download-print',
  templateUrl: './download-print.component.html',
})
@DestroySubscribers()
export class DownloadPrintComponent implements OnInit, AddSubscribers, OnDestroy {
  blanks$: Observable<BlankForm[]>;
  downloadBlanksGetRequestState$: Observable<RequestState<any>>;
  printBlankGetRequestState$: Observable<RequestState<any>>;
  downloadSubject$: Subject<boolean> = new Subject<boolean>();
  printSubject$: Subject<boolean> = new Subject<boolean>();

  formControl: FormControl;

  private subscribers: any = {};

  constructor(
    private packagesService: PackagesService,
    private printFormsSheetsService: PrintFormsSheetsService,
    private activeModal: NgbActiveModal,
  ) {
    this.formControl = new FormControl([], [this.isValidToPrint]);
  }

  ngOnInit() {
    this.blanks$ = this.printFormsSheetsService.blanks$;
    this.downloadBlanksGetRequestState$ = this.printFormsSheetsService.downloadBlanksGetRequestState$;
    this.printBlankGetRequestState$ = this.printFormsSheetsService.printBlankGetRequestState$;
  }

  addSubscribers() {
    this.subscribers.downloadFormGroupSubjectSubscription = this.downloadSubject$.pipe(
      withLatestFrom(this.packagesService.activePackageId$),
      switchMap(([, id]) => this.printFormsSheetsService.downloadBlanksForm({
          packageId: id,
          formIdList: this.formControl.value
        }).pipe(catchError(() => EMPTY)
        ))
    )
    .subscribe((data: any) => {
        FileSaver.saveAs(data.file, data.fileName);
      }
    );

    this.subscribers.printFormGroupSubjectSubscription = this.printSubject$.pipe(
      filter(() => this.formControl.valid),
      withLatestFrom(this.packagesService.activePackageId$),
      switchMap(([, id]) => this.printFormsSheetsService.printBlankForm({
          packageId: id,
          formId: this.formControl.value
        }).pipe(catchError(() => EMPTY)
        ))
    )
    .subscribe((data: any) => {
        const file = data.file;
        const fileType = file.type;
        const fileBlob = new Blob([file], {type: fileType});
        const fileURL = URL.createObjectURL(fileBlob);
        window.open(fileURL, '_blank');
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  onCheckChange(checkValue) {
    const formControlValue = this.formControl.value;
    const isValue = formControlValue.some((item) => item === checkValue);
    if (isValue) {
      const updatedFormControlValue = formControlValue.filter((item) => item !== checkValue);
      this.formControl.reset(updatedFormControlValue);
    } else if (!isValue) {
      this.formControl.patchValue([...formControlValue, checkValue]);
    }
  }

  isValidToPrint: ValidatorFn = (control: FormControl): ValidationErrors | null => control.value.length !== 1 ? {invalid: true} : null;

  closeModal() {
    this.activeModal.dismiss();
  }

  downloadFiles() {
    this.downloadSubject$.next(true);
  }

  printFiles() {
    this.printSubject$.next(true);
  }
}
