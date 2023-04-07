import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { SendToModule } from './send-to/send-to.module';
import { CreateAlertComponent } from './create-alert.component';
import { SourceAlertModule } from './source-alert/source-alert.module';


@NgModule({
  imports: [
    SharedModule,
    SendToModule,
    SourceAlertModule,
  ],
  exports: [
    CreateAlertComponent
  ],
  declarations: [
    CreateAlertComponent,
  ]
})
export class CreateAlertModule { }
