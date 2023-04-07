import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { TranslateModule } from '@ngx-translate/core';

import { FormlyModule } from '@ngx-formly/core';

import { FormlyComponentsModule } from '../components/formly-components.module';

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
import { BaseFieldWrapperComponent } from './base-field-wrapper';


@NgModule({
  imports: [
    CommonModule,
    FormlyModule,
    TranslateModule,
    FormlyComponentsModule,
    NgbModule
  ],
  declarations: [
    BaseFieldWrapperComponent,
    FormlyHorizontalWrapperComponent,
    FormlyVerticalWrapperComponent,
    ValidationMessageWrapperComponent,
    FieldRequestWrapperComponent,
    HelpPopupWrapperComponent,
    RepeatQuestionGroupWrapperComponent,

    EyeColorWrapperComponent,
    HairColorWrapperComponent,
    HeightWrapperComponent,
    RaceWrapperComponent,
    WeightWrapperComponent,
    AssetWarningWrapperComponent
  ],
  exports: [
    BaseFieldWrapperComponent,
    FormlyHorizontalWrapperComponent,
    FormlyVerticalWrapperComponent,
    ValidationMessageWrapperComponent,
    FieldRequestWrapperComponent,
    HelpPopupWrapperComponent,
    RepeatQuestionGroupWrapperComponent,

    EyeColorWrapperComponent,
    HairColorWrapperComponent,
    HeightWrapperComponent,
    RaceWrapperComponent,
    WeightWrapperComponent,
    AssetWarningWrapperComponent
  ],
})
export class FormlyWrappersModule {
}
