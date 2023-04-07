import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { State } from '../../core/ngrx/state';
import { FeeDetails } from '../../core/models/fee-details.model';
import { RequestState } from '../../core/ngrx/utils';
import { GovernmentFee } from '../../core/models/government-fee.model';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import {
  GetBatchJobsConfig,
  GetRankingData,
  GetRepresentativesCount, PatchBatchJobsConfig,
  PutRankingData
} from './ngrx/settings/settings.actions';
import { RankingData } from '../models/ranking-data.model';
import { RepresentativesCount } from '../models/representatives-count.model';
import { Job } from '../models/site-jobs';

import { governmentFeePostRequestHandler } from './ngrx/requests/government-fee-post/state';
import { feeDetailsPostRequestHandler } from '../../core/ngrx/config-data-requests/fee-details-post/state';
import {
  getGovernmentFeePostRequestState,
  getRankingData, getRankingDataGetRequestState,
  getRankingDataPutRequestState, getRepresentativesCountGetRequestState,
  getRepresentativesCount, getBatchJobsGetRequestState, getBatchJobsPatchRequestState, getBatchJobs
} from './ngrx/state';
import { selectFeeDetailsPostRequestState } from '../../core/ngrx/config-data-requests/state';
import { PostFeeDetails } from '../../core/ngrx/config-data/config-data.actions';


@Injectable()
export class DashboardSettingsService {

  feeDetailsPostState$: Observable<RequestState<FeeDetails>>;
  governmentFeePostState$: Observable<RequestState<GovernmentFee>>;
  rankingData$: Observable<RankingData>;
  rankingDataPutRequestState$: Observable<RequestState<RankingData>>;
  rankingDataGetRequestState$: Observable<RequestState<RankingData>>;
  representativesCount$: Observable<RepresentativesCount>;
  representativesCountGetRequestState$: Observable<RequestState<RepresentativesCount>>;
  batchJob$: Observable<Job>;
  batchJobGetRequestState$: Observable<RequestState<Job>>;
  batchJobPatchRequestState$: Observable<RequestState<Job>>;

  constructor(
    private store: Store<State>
  ) {
    this.feeDetailsPostState$ = this.store.pipe(select(selectFeeDetailsPostRequestState));
    this.governmentFeePostState$ = this.store.pipe(select(getGovernmentFeePostRequestState));
    this.rankingData$ = this.store.pipe(select(getRankingData));
    this.rankingDataPutRequestState$ = this.store.pipe(select(getRankingDataPutRequestState));
    this.rankingDataGetRequestState$ = this.store.pipe(select(getRankingDataGetRequestState));
    this.representativesCount$ = this.store.pipe(select(getRepresentativesCount));
    this.representativesCountGetRequestState$ = this.store.pipe(select(getRepresentativesCountGetRequestState));
    this.batchJob$ = this.store.pipe(select(getBatchJobs));
    this.batchJobGetRequestState$ = this.store.pipe(select(getBatchJobsGetRequestState));
    this.batchJobPatchRequestState$ = this.store.pipe(select(getBatchJobsPatchRequestState));
  }

  updateFeeDetails(feeDetails: FeeDetails) {
    this.store.dispatch(new PostFeeDetails(feeDetails));
    return this.feeDetailsPostState$;
  }

  updateGovernmentFee(governmentFee: GovernmentFee) {
    this.store.dispatch(governmentFeePostRequestHandler.requestAction(governmentFee));
    return this.governmentFeePostState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getRankingData() {
    this.store.dispatch(new GetRankingData());
    return this.rankingDataGetRequestState$;
  }

  updateRankingData(data: RankingData) {
    this.store.dispatch(new PutRankingData(data));
  }

  getRepresentativesCount() {
    this.store.dispatch(new GetRepresentativesCount());
  }

  getBatchJob() {
    this.store.dispatch(new GetBatchJobsConfig());
    return this.batchJobGetRequestState$;
  }

  updateBatchJob(job: Job) {
    this.store.dispatch(new PatchBatchJobsConfig(job));
  }
}
