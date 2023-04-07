import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { ClientsRoutingModule } from './clients-routing.module';
import { PackagesModule } from './packages/packages.module';

@NgModule({
  imports: [
    SharedModule,
    ClientsRoutingModule,
    PackagesModule,
  ],

})
export class ClientsModule {
}
