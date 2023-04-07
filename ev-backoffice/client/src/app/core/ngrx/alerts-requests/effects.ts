import { AlertPutRequestEffects } from './alert-put/state';
import { AlertsGetRequestEffects } from './alerts-get/state';
import { AlertsDeleteRequestEffects } from './alerts-delete/state';
import { SendAlertPostRequestEffects } from './send-alert-post/state';

export const AlertsRequestEffects = [
  AlertPutRequestEffects,
  AlertsGetRequestEffects,
  AlertsDeleteRequestEffects,
  SendAlertPostRequestEffects,
];
