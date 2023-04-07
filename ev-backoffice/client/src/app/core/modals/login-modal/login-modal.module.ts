import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';
import { AuthFormFieldModule } from '../../../components/auth-form-field/auth-form-field.module';

import { LoginModalComponent } from './login-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    AuthFormFieldModule,
  ],
  declarations: [
    LoginModalComponent,
  ],
  exports: [
    LoginModalComponent,
  ],
  entryComponents: [
    LoginModalComponent,
  ],
})
export class LoginModalModule {
}
