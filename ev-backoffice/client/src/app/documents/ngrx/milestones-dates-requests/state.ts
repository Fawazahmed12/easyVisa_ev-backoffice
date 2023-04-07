import { createFeatureSelector } from '@ngrx/store';

import { RequestState } from '../../../core/ngrx/utils';

import { MilestoneDate } from '../../models/milestone-date.model';


export const MILESTONES_DATES_REQUESTS = 'MilestoneDatesRequests';

export interface MilestoneDatesRequestState {
  milestoneDatesGet?: RequestState<MilestoneDate[]>;
  milestoneDatePost?: RequestState<MilestoneDate>;
}

export const selectMilestoneDatesModuleRequestsState = createFeatureSelector<MilestoneDatesRequestState>(MILESTONES_DATES_REQUESTS);

export const selectMilestoneDatesGetRequestState = (state: MilestoneDatesRequestState) => state.milestoneDatesGet;
export const selectMilestoneDatePostRequestState = (state: MilestoneDatesRequestState) => state.milestoneDatePost;


export { milestoneDatesGetRequestHandler } from './milestone-dates-get/state';
export { milestoneDatePostRequestHandler } from './milestone-date-post/state';


