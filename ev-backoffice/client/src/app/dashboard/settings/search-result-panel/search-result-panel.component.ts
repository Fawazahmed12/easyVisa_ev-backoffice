import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { combineLatest, Observable, Subject } from 'rxjs';

import { isEqual } from 'lodash-es';

import {
  filter,
  map,
  pluck,
  publishReplay,
  refCount,
  startWith,
  switchMapTo,
} from 'rxjs/operators';

import { RankingData } from '../../models/ranking-data.model';

import { DashboardSettingsService } from '../settings.service';


@Component({
  selector: 'app-search-result-panel',
  templateUrl: './search-result-panel.component.html',
})
@DestroySubscribers()
export class SearchResultPanelComponent implements OnInit, AddSubscribers, OnDestroy {
  rankingDataFormGroup: FormGroup;
  rankingData$: Observable<RankingData>;
  isRankingDataPutLoading$: Observable<boolean>;
  isRevertButtonDisabled$: Observable<boolean>;
  submitForm$ = new Subject<RankingData>();
  resetForm$ = new Subject<boolean>();

  private subscribers: any = {};

  constructor(
    private dashboardSettingsService: DashboardSettingsService,
  ) {
    this.createRankingDataFormGroup(null);
  }

  get formValue$() {
    return this.rankingDataFormGroup.valueChanges.pipe(
      startWith(this.rankingDataFormGroup.value),
      publishReplay(1),
      refCount(),
      );
  }

  ngOnInit() {
    this.rankingData$ = this.dashboardSettingsService.rankingData$;
    this.isRankingDataPutLoading$ = this.dashboardSettingsService.rankingDataPutRequestState$.pipe(
      pluck('loading')
    );

    this.isRevertButtonDisabled$ = combineLatest([
      this.rankingData$,
      this.formValue$
    ]).pipe(
      map(([feeDetails, formValue]) => isEqual(feeDetails, formValue)),
      publishReplay(1),
    );
  }

  addSubscribers() {
    this.subscribers.rankikngDataSubscription = this.rankingData$.pipe(
      filter((rankingData) => !!rankingData)
    ).subscribe((rankingData) => this.resetFormGroup(rankingData));

    this.subscribers.resetFormSubscription = this.resetForm$.pipe(
      switchMapTo(this.rankingData$)
    ).subscribe((rankingData) => {
      this.resetFormGroup(rankingData);
    });

    this.subscribers.submitSubscription = this.submitForm$.pipe(
      filter(() => this.rankingDataFormGroup.valid),
    ).subscribe((value) => this.dashboardSettingsService.updateRankingData(value));
  }


  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createRankingDataFormGroup(data) {
    this.rankingDataFormGroup = new FormGroup({
      recentContributor: this.createUnitFormGroup(data && data.recentContributor ? data.recentContributor : null),
      topContributor: this.createUnitFormGroup(data && data.topContributor ? data.topContributor : null),
      allReps: this.createUnitFormGroup(data && data.allReps ? data.allReps : null),
    });
  }

  createUnitFormGroup(data) {
    return new FormGroup({
      points: new FormControl(data && data.points ? data.points : null),
      articlesInMonth: new FormControl(data && data.articlesInMonth ? data.articlesInMonth : null),
      articlesInQuarter: new FormControl(data && data.articlesInQuarter ? data.articlesInQuarter : null),
      articlesInHalf: new FormControl(data && data.articlesInHalf ? data.articlesInHalf : null),
    });
  }

  private resetFormGroup(data: RankingData) {
    this.rankingDataFormGroup.reset(data);
  }

  resetForm() {
    this.resetForm$.next(null);
  }

  onSubmit() {
    this.submitForm$.next(this.rankingDataFormGroup.value);
  }
}

