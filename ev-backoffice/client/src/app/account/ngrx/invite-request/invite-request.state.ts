import { createFeatureSelector } from '@ngrx/store';

import { Invite } from '../../models/invite.model';
import { RequestJoin } from '../../models/request-join.model';

export const INVITE_REQUEST = 'InviteRequest';

export interface InviteRequestState {
  invite: Invite;
  request: RequestJoin;
}

export const selectInviteRequestState = createFeatureSelector<InviteRequestState>(INVITE_REQUEST);

export const selectInvite = ({invite}: InviteRequestState) => invite;

export const selectRequest = ({request}: InviteRequestState) => request;
