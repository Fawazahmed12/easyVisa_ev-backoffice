import { FormlyHorizontalWrapperComponent } from './horizontal-wrappers';
import { FormlyVerticalWrapperComponent } from './vertical-wrappers';
import { ValidationMessageWrapperComponent } from './validation-message-wrapper';
import { FieldRequestWrapperComponent } from './field-request-wrapper';
import { HelpPopupWrapperComponent } from './help-popup-wrapper';
import { RepeatQuestionGroupWrapperComponent } from './repeat-question-group-wrapper';

import { EyeColorWrapperComponent } from './sections/biographic-information/eyecolor-wrapper';
import { HairColorWrapperComponent } from './sections/biographic-information/haircolor-wrapper';
import { HeightWrapperComponent } from './sections/biographic-information/height-wrapper';
import { RaceWrapperComponent } from './sections/biographic-information/race-wrapper';
import { WeightWrapperComponent } from './sections/biographic-information/weight-wrapper';
import { AssetWarningWrapperComponent } from './sections/assets/asset-warning-wrapper';

export { FormlyWrappersModule } from './formly-wrappers.module';

export const FORMLY_WRAPPERS = [
  { name: 'form-field-horizontal', component: FormlyHorizontalWrapperComponent },
  { name: 'form-field-vertical', component: FormlyVerticalWrapperComponent },
  { name: 'validation-message', component: ValidationMessageWrapperComponent },
  { name: 'request', component: FieldRequestWrapperComponent },
  { name: 'help-popup', component: HelpPopupWrapperComponent },
  { name: 'repeat-question-group', component: RepeatQuestionGroupWrapperComponent },

  { name: 'eye-color', component: EyeColorWrapperComponent },
  { name: 'hair-color', component: HairColorWrapperComponent },
  { name: 'height', component: HeightWrapperComponent },
  { name: 'race', component: RaceWrapperComponent },
  { name: 'weight', component: WeightWrapperComponent },
  { name: 'asset-warning-info', component: AssetWarningWrapperComponent }
];
