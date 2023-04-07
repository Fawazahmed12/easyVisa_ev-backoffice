import { ApplicantGetRequestEffects } from './applicant-get/state';
import { ApplicantInvitePostRequestEffects } from './applicant-invite-post/state';
import { AlertsDeleteRequestEffects } from '../../../core/ngrx/alerts-requests/alerts-delete/state';
import { AlertsGetRequestEffects } from '../../../core/ngrx/alerts-requests/alerts-get/state';
import { AlertPutRequestEffects } from '../../../core/ngrx/alerts-requests/alert-put/state';
import { ChangePackageStatusPostRequestEffects } from './change-package-status-post/state';
import { PackageGetRequestEffects } from './package-get/state';
import { PackagePatchRequestEffects } from './package-patch/state';
import { PackagePostRequestEffects } from './package-post/state';
import { PackageWelcomeEmailPostRequestEffects } from './package-welcome-email-post/state';
import { RetainerAgreementPostRequestEffects } from './retainer-agreement-post/state';
import { RetainerAgreementDeleteRequestEffects } from './retainer-agreement-delete/state';
import { LeadPackagesDeleteRequestEffects } from './lead-packages-delete/state';
import { WarningsDeleteRequestEffects } from './warnings-delete/state';
import { WarningsGetRequestEffects } from './warnings-get/state';
import { WarningPutRequestEffects } from './warning-put/state';
import { FeesBillPostRequestEffects } from './fees-bill-post/state';
import { SendAlertPostRequestEffects } from '../../../core/ngrx/alerts-requests/send-alert-post/state';
import { DispositionsGetRequestEffects } from './dispositions-get/state';
import { DispositionDataGetRequestEffects } from './disposition-data-get/state';
import { DispositionPutRequestEffects } from './disposition-put/state';
import { SelectedLeadPackagesDeleteRequestEffects } from './selected-lead-packages-delete/state';
import { SelectedTransferredPackagesDeleteRequestEffects } from './selected-transferred-packages-delete/state';
import { ChangePackageOwedPatchRequestEffects } from './change-package-owed-patch/state';

export const TaskQueueModuleRequestEffects = [
  ApplicantGetRequestEffects,
  ApplicantInvitePostRequestEffects,
  AlertsDeleteRequestEffects,
  AlertsGetRequestEffects,
  AlertPutRequestEffects,
  ChangePackageStatusPostRequestEffects,
  PackageGetRequestEffects,
  PackagePatchRequestEffects,
  PackagePostRequestEffects,
  PackageWelcomeEmailPostRequestEffects,
  RetainerAgreementPostRequestEffects,
  RetainerAgreementDeleteRequestEffects,
  LeadPackagesDeleteRequestEffects,
  WarningsDeleteRequestEffects,
  WarningsGetRequestEffects,
  WarningPutRequestEffects,
  FeesBillPostRequestEffects,
  SendAlertPostRequestEffects,
  DispositionsGetRequestEffects,
  DispositionDataGetRequestEffects,
  DispositionPutRequestEffects,
  SelectedLeadPackagesDeleteRequestEffects,
  SelectedTransferredPackagesDeleteRequestEffects,
  ChangePackageOwedPatchRequestEffects
];
