import { NgModule } from '@angular/core';

import { FormlyModule } from '@ngx-formly/core';
import { FormlyBootstrapModule } from '@ngx-formly/bootstrap';

import { FormlyComponentsModule } from './components/formly-components.module';

import { FormlyWrappersModule } from './wrappers';
import { ValidationMessageWrapperComponent } from './wrappers/validation-message-wrapper';

import { SectionModule } from './section/section.module';

@NgModule({
  imports: [
    // Used to change native validation message wrapper to translated one
    FormlyModule.forChild({
      wrappers: [
        {name: 'validation-message', component: ValidationMessageWrapperComponent},
      ],
    }),
  ],
  exports: [
    SectionModule,
    FormlyModule,
    FormlyBootstrapModule,
    FormlyComponentsModule,
    FormlyWrappersModule,
  ],
})
export class SharedFormlyModule {
}
