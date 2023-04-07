import { Injectable } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';
import { DATE_PATTERN } from '../../../shared/validators/constants/date-pattern.const';

@Injectable()
export class UscisDatesFormGroupService {

  private resetFormGroup$: Subject<void> = new Subject();
  formGroup: FormGroup;

  constructor() {
    this.resetFormGroup$
      .subscribe(() => {
        this.formGroup.setControl('uscisForms', new FormArray(this.createUscisFormGroup(null)));
      });
  }

  get onFormGroupReset$() {
    return this.resetFormGroup$.asObservable();
  }

  createFormGroup(data) {
    this.formGroup = new FormGroup({
      id: new FormControl({ value: data && data.id, disabled: !data }),
      uscisForms: new FormArray(
        data && data.uscisForms.length ?  this.createUscisFormGroup(data.uscisForms)  : []),
    });
  }

  createUscisFormGroup(uscisForms) {
    const uscisFormGroups = [];
    uscisForms.forEach((uscisForm) => {
      uscisFormGroups.push(
        new FormGroup({
          editionDate: new FormControl(uscisForm ? uscisForm.editionDate : null, {
            validators: [
              Validators.required,
              Validators.pattern(DATE_PATTERN),
            ],
          }),
          expirationDate: new FormControl(uscisForm ? uscisForm.expirationDate : null, {
            validators: [
              Validators.required,
              Validators.pattern(DATE_PATTERN),
            ],
          }),
          displayText: new FormControl(uscisForm ? uscisForm.displayText : null),
          formId: new FormControl(uscisForm ? uscisForm.formId : null),
        })
      );
    });
    return uscisFormGroups;
  }

  resetFormGroup() {
    this.resetFormGroup$.next();
  }
}
