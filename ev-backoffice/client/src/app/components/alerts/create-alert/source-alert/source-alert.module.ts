import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';

import { SourceAlertComponent } from './source-alert.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    SourceAlertComponent
  ],
  exports: [
    SourceAlertComponent
  ],
  entryComponents: [
    SourceAlertComponent
  ]
})
export class SourceAlertModule { }
