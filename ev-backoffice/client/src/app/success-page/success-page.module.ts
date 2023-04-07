import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { AuthWrapperModule } from '../auth/components/auth-wrapper/auth-wrapper.module';

import { SuccessPageRoutingModule } from './success-page-routing.module';
import { SuccessPageComponent } from './success-page.component';

@NgModule({
  imports: [
    SharedModule,
    SuccessPageRoutingModule,
    AuthWrapperModule,
  ],
  declarations: [
    SuccessPageComponent,
  ],
})
export class SuccessPageModule {
}
