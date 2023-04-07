import { NgModule } from '@angular/core';
import { ProfilePreviewComponent } from './profile-preview.component';
import { SharedModule } from '../../../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../../../shared/pipes/find-label/find-label-pipe.module';
import { TransformTimePipeModule } from './transform-time-pipe/transform-time-pipe.module';
import { NumberOfYearsPipe } from '../profile-edit/license-form/pipes/number-of-years.pipe';


@NgModule({
  imports: [
    SharedModule,
    FindLabelPipeModule,
    TransformTimePipeModule,
  ],
  declarations: [
    ProfilePreviewComponent,
  ],
  exports: [
    ProfilePreviewComponent,
  ],
  entryComponents: [
    ProfilePreviewComponent,
  ],
  providers: [
    NumberOfYearsPipe
  ]
})

export class ProfilePreviewModule {
}
