import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { TableComponent } from './table.component';
import { StarsRatingModule } from '../stars-rating/stars-rating.module';

@NgModule({
  imports: [
    SharedModule,
    StarsRatingModule,
    StarsRatingModule
  ],
  declarations: [
    TableComponent,
  ],
  exports: [
    TableComponent,
  ],
})
export class TableModule { }
