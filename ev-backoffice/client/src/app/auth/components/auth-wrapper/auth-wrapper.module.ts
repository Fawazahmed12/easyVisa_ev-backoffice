import { NgModule } from '@angular/core';
import { SharedModule } from '../../../shared/shared.module';
import { AuthWrapperComponent } from './auth-wrapper.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    AuthWrapperComponent,
  ],
  exports: [
    AuthWrapperComponent,
  ]
})
export class AuthWrapperModule {
}
