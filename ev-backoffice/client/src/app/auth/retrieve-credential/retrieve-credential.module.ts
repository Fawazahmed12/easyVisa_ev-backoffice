import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';

import { RetrieveCredentialRoutingModule } from './retrieve-credential-routing.module';
import { RetrieveCredentialComponent } from './retrieve-credential.component';
import { RetrieveCredentialService } from './retrieve-credential.service';

@NgModule({
  imports: [
    SharedModule,
    RetrieveCredentialRoutingModule,
    AuthWrapperModule,
  ],
  declarations: [
    RetrieveCredentialComponent,
  ],
  providers: [
    RetrieveCredentialService,
  ],
})
export class RetrieveCredentialModule {
}
