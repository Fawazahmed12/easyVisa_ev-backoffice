import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';

import { AlertHandlingRoutingModule } from './alert-handling-routing.module';
import { AlertReplyModule } from './alert-reply/alert-reply.module';


@NgModule({
  imports: [
    SharedModule,
    AlertReplyModule,
    AlertHandlingRoutingModule,
  ],
})
export class AlertHandlingModule {
}
