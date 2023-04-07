import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { ClientWelcomeRoutingModule } from './client-welcome-routing.module';
import { ClientWelcomeComponent } from './client-welcome.component';

@NgModule({
  imports: [
    SharedModule,
    ClientWelcomeRoutingModule,
  ],
  declarations: [
    ClientWelcomeComponent,
  ],
})
export class ClientWelcomeModule {
}
