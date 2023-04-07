import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';
import { SendToComponent } from './send-to.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    SendToComponent,
  ],
  exports: [
    SendToComponent
  ],
  entryComponents: [
    SendToComponent
  ]
})
export class SendToModule { }
