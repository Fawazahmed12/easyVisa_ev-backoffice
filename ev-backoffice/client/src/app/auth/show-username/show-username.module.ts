import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { ShowUsernameComponent } from './show-username.component';
import { ShowUsernameRoutingModule } from './show-username-routing.module';
import { ShowUsernameService } from './show-username.service';
import { ShowUsernameResolverService } from './show-username-resolver.service';
import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';

@NgModule({
  imports: [
    SharedModule,
    ShowUsernameRoutingModule,
    AuthWrapperModule,
  ],
  declarations: [
    ShowUsernameComponent,
  ],
  providers: [
    ShowUsernameService,
    ShowUsernameResolverService,
  ],
})
export class ShowUsernameModule {
}
