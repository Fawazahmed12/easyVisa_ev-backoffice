import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { SocialMediaFormComponent } from './social-media-form.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    SocialMediaFormComponent,
  ],
  exports: [
    SocialMediaFormComponent,
  ]
})

export class SocialMediaFormModule {
}
