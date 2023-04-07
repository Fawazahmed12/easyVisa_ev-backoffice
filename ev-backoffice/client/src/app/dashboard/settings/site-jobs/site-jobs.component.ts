import { Component, OnInit } from '@angular/core';

import { distinctUntilChanged, filter, map } from 'rxjs/operators';
import { merge, Observable } from 'rxjs';

import { Job } from '../../models/site-jobs';

import { DashboardSettingsService } from '../settings.service';



@Component({
  selector: 'app-site-jobs',
  templateUrl: './site-jobs.component.html',
})
export class SiteJobsComponent implements OnInit {
  batchJob$: Observable<Job>;
  isBatchJobLoading$: Observable<boolean>;

  constructor(
    private dashboardSettingsService: DashboardSettingsService,
  ) {
  }

  ngOnInit() {
    this.batchJob$ = this.dashboardSettingsService.batchJob$.pipe(
      filter((job) => !!job),
    );

    this.isBatchJobLoading$ = merge(
      this.dashboardSettingsService.batchJobGetRequestState$,
      this.dashboardSettingsService.batchJobPatchRequestState$,
    ).pipe(
      map((state) => state.loading),
      distinctUntilChanged(),
    );
  }

  batchJobEnabledChange(event) {
    this.dashboardSettingsService.updateBatchJob({enable: event.target.checked});
  }
}

