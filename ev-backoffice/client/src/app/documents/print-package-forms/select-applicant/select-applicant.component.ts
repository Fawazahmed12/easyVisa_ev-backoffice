import { Component, forwardRef, OnInit } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

import { Observable } from 'rxjs';

import { PrintFormsSheetsService } from '../../services/print-forms-sheets.service';


@Component({
  selector: 'app-select-applicant',
  templateUrl: './select-applicant.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectApplicantComponent),
      multi: true
    }
  ]
})

export class SelectApplicantComponent implements ControlValueAccessor, OnInit {
  packageApplicants$: Observable<any[]>;
  formControl = new FormControl([]);

  onChange: (value: string[]) => void;
  onTouched: () => void;

  ids = [];
  disabled = false;

  constructor(
    private printFormsSheetsService: PrintFormsSheetsService,
  ) {
  }

  ngOnInit() {
    this.packageApplicants$ = this.printFormsSheetsService.packageApplicants$;
  }

  onCheckChange(checkValue) {
    const isValue = this.ids.includes(checkValue);
    if (isValue) {
      this.ids = this.ids.filter((item) => item !== checkValue);
    } else {
      this.ids = [...this.ids, checkValue];
    }
    this.onChange(this.ids);
  }

  writeValue(value) {
    this.ids = [...value];
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    isDisabled ? this.disabled = true : this.disabled = false;
  }
}
