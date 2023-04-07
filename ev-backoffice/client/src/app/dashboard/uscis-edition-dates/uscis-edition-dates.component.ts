import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { EMPTY, Observable, of } from 'rxjs';

import { TableHeader } from '../../components/table/models/table-header.model';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { ActivatedRoute, Router } from '@angular/router';
import { UscisEditionDatesService } from '../../core/services';
import { catchError, filter, map, switchMap, take } from 'rxjs/operators';
import { UscisEditionDatesModel } from '../../core/models/uscis-edition-dates.model';


@Component({
  selector: 'app-uscis-edition-dates',
  templateUrl: './uscis-edition-dates.component.html',
  styleUrls: [ './uscis-edition-dates.component.scss' ]
})
@DestroySubscribers()
export class UscisEditionDatesComponent implements OnInit, OnDestroy, AddSubscribers {

  formGroup: FormGroup;
  USCISEditionDatesTableHeader$: Observable<TableHeader[]>;
  USCISEditionDatesTableData$: Observable<UscisEditionDatesModel[]>;

  private subscribers: any = {};

  get sortFormControl() {
    return this.formGroup.get('sort');
  }

  get orderFormControl() {
    return this.formGroup.get('order');
  }


  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private uscisEditionDatesService: UscisEditionDatesService) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.USCISEditionDatesTableHeader$ = of(
      [
        {
          title: 'TEMPLATE.DASHBOARD.USCIS_EDITION_DATES.USCIS_FORM_NO_NAME',
          colName: 'displayText',
          sortBy: true,
          colClass: 'width-70'
        },
        {
          title: 'TEMPLATE.DASHBOARD.USCIS_EDITION_DATES.EDITION_DATE',
          colName: 'editionDate',
          sortBy: true,
          colClass: 'text-center width-15'
        },
        {
          title: 'TEMPLATE.DASHBOARD.USCIS_EDITION_DATES.EXPIRATION_DATE',
          colName: 'expirationDate',
          sortBy: true,
          colClass: 'text-center width-15'
        }
      ]);

    this.USCISEditionDatesTableData$ = this.uscisEditionDatesService.uscisEditionDates$.pipe(
      filter((uscisEditionDates: any) => !!uscisEditionDates),
      map((uscisEditionDates: any) => uscisEditionDates.map((uscisEditionDate) => ({
            formId: { data: uscisEditionDate.formId },
            name: { data: uscisEditionDate.name },
            displayText: { data: uscisEditionDate.displayText },
            editionDate: { data: uscisEditionDate.editionDate },
            expirationDate: { data: uscisEditionDate.expirationDate }
          })))
    );
  }

  addSubscribers() {
    this.subscribers.getWarningsFormGroupSubscription = this.formGroup.valueChanges.pipe(
      switchMap(() => this.uscisEditionDatesService.getUscisEditionDates(this.formGroup.getRawValue()).pipe(
        filter((uscisEditionDates: any) => !!uscisEditionDates),
        catchError(() => {
          this.router.navigate([]);
          return EMPTY;
        }),
        take(1),
      )),
    ).subscribe();


    this.subscribers.formGroupForaddQuerySubscription = this.formGroup.valueChanges.pipe(
    ).subscribe(() => {
      this.addQueryParamsToUrl({
        ...this.formGroup.getRawValue()
      });
    });
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      sort: new FormControl('editionDate'),
      order: new FormControl('asc')
    });
  }

  sortBy(colName) {
   if (colName !== this.sortFormControl.value) {
      this.formGroup.patchValue({ sort: colName });
    } else {
      this.formGroup.patchValue({ order: this.orderFormControl.value === 'asc' ? 'desc' : 'asc' });
    }
  }

  addQueryParamsToUrl(params?) {
    this.router.navigate([ './' ], { relativeTo: this.activatedRoute, queryParams: { ...params } });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

}
