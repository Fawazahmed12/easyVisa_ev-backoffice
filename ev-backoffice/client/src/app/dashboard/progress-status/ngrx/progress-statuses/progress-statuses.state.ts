import { createFeatureSelector } from '@ngrx/store';

import { ProgressStatus } from '../../models/progress-status.model';

export const PROGRESS_STATUSES = 'ProgressStatuses';

export interface ProgressStatusesState {
  questionnaireProgress: ProgressStatus[];
  documentProgress: ProgressStatus[];
}

export const selectProgressStatusesState = createFeatureSelector<ProgressStatusesState>(PROGRESS_STATUSES);

export const selectQuestionnaireProgress = ({questionnaireProgress}: ProgressStatusesState) => questionnaireProgress;
export const selectDocumentProgress = ({documentProgress}: ProgressStatusesState) => documentProgress;
