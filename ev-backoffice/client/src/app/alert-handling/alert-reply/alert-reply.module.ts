import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { SpinnerModule } from '../../components/spinner/spinner.module';

import { AlertReplyComponent } from './alert-reply.component';
import { AlertHandlingService } from '../../core/services/alert-handling.service';

@NgModule({
  imports: [
    SharedModule,
    SpinnerModule,
  ],
  declarations: [
    AlertReplyComponent,
  ],
  providers: [
    AlertHandlingService
  ],
})
export class AlertReplyModule { }
