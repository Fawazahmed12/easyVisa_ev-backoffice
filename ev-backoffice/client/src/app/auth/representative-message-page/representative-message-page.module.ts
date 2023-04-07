import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { RepresentativeMessagePageRoutingModule } from './representative-message-page-routing.module';
import { RepresentativeMessagePageComponent } from './representative-message-page.component';

@NgModule({
  imports: [
    SharedModule,
    RepresentativeMessagePageRoutingModule,
  ],
  declarations: [
    RepresentativeMessagePageComponent,
  ]
})
export class RepresentativeMessagePageModule {
}
