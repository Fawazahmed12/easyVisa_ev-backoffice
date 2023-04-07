import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { ValueExistenceMessagesComponent } from './value-existence-messages.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    ValueExistenceMessagesComponent,
  ],
  exports: [
    ValueExistenceMessagesComponent,
  ],
})
export class ValueExistenceMessagesModule {}
