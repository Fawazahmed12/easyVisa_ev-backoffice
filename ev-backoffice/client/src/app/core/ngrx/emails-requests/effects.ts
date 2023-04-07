import { EmailByIdGetRequestEffects } from './email-by-id-get/state';
import { EmailPostRequestEffects } from './email-post/state';
import { EmailPutRequestEffects } from './email-put/state';
import { PreviewUnsavedEmailPostRequestEffects } from './preview-unsaved-email-post/state';

export const EmailsRequestEffects = [
  EmailByIdGetRequestEffects,
  EmailPostRequestEffects,
  EmailPutRequestEffects,
  PreviewUnsavedEmailPostRequestEffects,
];
