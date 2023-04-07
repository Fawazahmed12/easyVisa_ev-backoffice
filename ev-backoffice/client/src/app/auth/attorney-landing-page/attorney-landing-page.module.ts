import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { AttorneyLandingPageRoutingModule } from './attorney-landing-page-routing.module';
import { AttorneyLandingPageComponent } from './attorney-landing-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CardModule } from './card/card.module';

@NgModule({
  imports: [
    SharedModule,
    AttorneyLandingPageRoutingModule,
    ReactiveFormsModule,
    CardModule
  ],
  declarations: [
    AttorneyLandingPageComponent,
  ]
})
export class AttorneyLandingPageModule {
}
