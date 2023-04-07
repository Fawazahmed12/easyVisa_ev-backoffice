import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { AttorneyWelcomeRoutingModule } from './attorney-welcome-routing.module';
import { AttorneyWelcomeComponent } from './attorney-welcome.component';

@NgModule({
  imports: [
    SharedModule,
    AttorneyWelcomeRoutingModule,
  ],
  declarations: [
    AttorneyWelcomeComponent,
  ],
})
export class AttorneyWelcomeModule {
}
