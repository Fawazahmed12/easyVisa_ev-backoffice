import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { SignUpService } from '../../../../../../../auth/services';

import { ChangeEmailComponent } from './change-email.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    ChangeEmailComponent,
  ],
  exports: [
    ChangeEmailComponent,
  ],
  providers: [
    SignUpService
  ]
})

export class ChangeEmailModule {
}
