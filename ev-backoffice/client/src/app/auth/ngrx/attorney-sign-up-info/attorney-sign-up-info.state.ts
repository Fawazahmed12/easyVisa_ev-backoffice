import { State } from '../state';
import { Attorney } from '../../../core/models/attorney.model';

export const ATTORNEY_SIGN_UP_INFO = 'AttorneySignUpInfo';

export interface AttorneySignUpInfoState {
  referringUserName: string;
  referralEmail: string;
  attorneySignUpInfo: Attorney;
}

export const selectAttorneySignUpInfoState = (state: State) => state[ATTORNEY_SIGN_UP_INFO];

export const selectReferringUserName = ({referringUserName}: AttorneySignUpInfoState) => referringUserName;

export const selectAttorneySignUpInfo = ({attorneySignUpInfo}: AttorneySignUpInfoState) => attorneySignUpInfo;

export const selectReferralEmail = ({referralEmail}: AttorneySignUpInfoState) => referralEmail;
