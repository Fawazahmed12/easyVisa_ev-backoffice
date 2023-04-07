import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import {FormArray, FormGroup} from '@angular/forms';
import { UscisDatesFormGroupService } from '../../services/uscis-dates-form-group.service';
import { UscisEditionDatesService } from '../../../../core/services';
import {RequestState} from '../../../../core/ngrx/utils';
import {UscisEditionDatesModel} from '../../../../core/models/uscis-edition-dates.model';

@Component({
  selector: 'app-uscis-edition-dates-editor',
  templateUrl: './uscis-edition-dates-editor.component.html'
})
export class UscisEditionDatesEditorComponent implements OnInit {

  submitted = false;
  isLoading$: Observable<any>;
  putUscisEditionDatesRequest$: Observable<RequestState<UscisEditionDatesModel[]>>;

  get uscisFormsFormGroups() {
    return this.uscisFormsFormArray.controls as [FormGroup];
  }

  get uscisFormsFormArray() {
    return this.uscisDatesFormGroupService.formGroup.get('uscisForms') as FormArray;
  }

  constructor(private uscisEditionDatesService: UscisEditionDatesService,
              private uscisDatesFormGroupService: UscisDatesFormGroupService) {
  }

  ngOnInit() {
    this.putUscisEditionDatesRequest$ = this.uscisEditionDatesService.putUscisEditionDatesRequest$;
    this.isLoading$ =  this.putUscisEditionDatesRequest$.pipe(map((request) => request.loading));

    this.uscisDatesFormGroupService.createFormGroup({uscisForms: []});
    this.uscisEditionDatesService.getUscisEditionDates().subscribe((data) => {
      this.uscisDatesFormGroupService.createFormGroup({uscisForms: data});
    });
  }

  saveUscisEditionDates() {
    this.submitted = true;
    if (this.uscisFormsFormArray.invalid) {
      return;
    }


    const uscisForms = this.uscisFormsFormArray.getRawValue();
    const uscisEditionDateList = uscisForms.map((data) => ({
        editionDate: data.editionDate,
        expirationDate: data.expirationDate,
        formId: data.formId
      }));
    this.uscisEditionDatesService.updateUscisEditionDates({uscisEditionDateList})
      .pipe(tap(() => {
        this.submitted = false;
      }));
  }
}
