import { NgModule } from '@angular/core';

import { NgbRatingModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { StarsRatingComponent } from './stars-rating.component';

@NgModule({
  imports: [
    SharedModule,
    NgbRatingModule
  ],
  declarations: [
    StarsRatingComponent,
  ],
  exports: [
    StarsRatingComponent,
  ]
})
export class StarsRatingModule { }
