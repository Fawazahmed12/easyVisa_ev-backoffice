import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup } from '@angular/forms';

import { Observable } from 'rxjs';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { debounceTime, distinctUntilChanged, filter, map, switchMap, take, withLatestFrom } from 'rxjs/operators';

import { differenceBy, last, forIn, isEqual } from 'lodash-es';

import { ModalService, PackagesService } from '../../core/services';

import { MilestoneDate } from '../models/milestone-date.model';
import { MilestoneDatesService } from '../services/milestone-dates.service';
import { PostMilestoneDateFailure } from '../ngrx/milestone-dates/milestone-dates.actions';

@Component({
  selector: 'app-milestone-dates',
  templateUrl: './milestone-dates.component.html',
})
@DestroySubscribers()
export class MilestoneDatesComponent implements OnInit, AddSubscribers, OnDestroy {

  @Input() readOnlyAccess;
  milestoneDates$: Observable<MilestoneDate[]>;
  formGroup: FormGroup;
  formArray = new FormArray([]);
  content: ({ dataLabel: string; formControl: AbstractControl; desc: string })[];

  private subscribers: any = {};

  get milestoneDateFormGroups() {
    return this.formArray as FormArray;
  }

  constructor(
    private packagesService: PackagesService,
    private milestoneDatesService: MilestoneDatesService,
    private modalService: ModalService
  ) {
  }

  ngOnInit() {
    this.milestoneDates$ = this.milestoneDatesService.milestoneDates$;
  }

  addSubscribers() {
    this.subscribers.milestoneDatesSubscription = this.milestoneDates$.pipe(
      filter((milestoneDates) => !!milestoneDates),
      distinctUntilChanged(isEqual),
      take(1),
    ).subscribe((milestoneDates) => {
      milestoneDates.forEach((milestoneDate) => this.formArray.push(this.createFormGroup(milestoneDate)));
    });

    this.subscribers.milestoneDateSaveSubscription = this.milestoneDateFormGroups.valueChanges.pipe(
      debounceTime(1000),
      withLatestFrom(
        this.milestoneDates$,
        this.packagesService.activePackageId$,
      ),
      map(([ formArray, milestoneDates, activePackageId ]) => [
        differenceBy(formArray, milestoneDates, 'milestoneDate'), activePackageId
      ]),
      switchMap(([ newDate, packageId ]) => {
        const date = last(newDate);
        const { milestoneTypeId, milestoneDate, ...rest } = date;
        return this.milestoneDatesService.postMilestoneDate({
          milestoneTypeId,
          milestoneDate,
          packageId
        });
      }),
    ).subscribe();

    this.subscribers.milestoneDatesFailureSubscription = this.milestoneDatesService.postMilestoneDateFailAction$
      .pipe(filter((action: PostMilestoneDateFailure) => this.milestoneDatesService.documentAccessErrorFilter(action)))
      .subscribe((data) => this.milestoneDatesService.documentAccessErrorHandler(data));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
    forIn(this.subscribers, ( val, ) => val.unsubscribe && val.unsubscribe());
  }

  createFormGroup(data: MilestoneDate) {
    return new FormGroup({
        milestoneTypeId: new FormControl(data.milestoneTypeId),
        description: new FormControl(data.description),
        dataLabel: new FormControl(data.dataLabel),
        milestoneDate: new FormControl({ value: data.milestoneDate, disabled: this.readOnlyAccess }),
      }
    );
  }
}
