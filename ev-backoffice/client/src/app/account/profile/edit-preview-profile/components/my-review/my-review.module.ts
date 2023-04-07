import { NgModule } from '@angular/core';
import { DatePipe } from '@angular/common';

import { NgbPaginationModule, NgbProgressbarModule, NgbRatingModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../../../../shared/shared.module';
import { BlockModule } from '../../../../../shared/components/block/block.module';
import { TableModule } from '../../../../../components/table/table.module';
import { StarsRatingModule } from '../../../../../components/stars-rating/stars-rating.module';
import { NumberHalfRoundPipeModule } from '../../../../../shared/pipes/number-half-round/number-half-round-pipe.module';

import { ReviewService } from '../../../../services/review.service';

import { WriteReviewModule } from './modals/write-reply/write-reply.module';
import { MyReviewComponent } from './my-review.component';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
    TableModule,
    NgbPaginationModule,
    NgbTooltipModule,
    NgbProgressbarModule,
    NgbRatingModule,
    StarsRatingModule,
    NumberHalfRoundPipeModule,
    WriteReviewModule
  ],
  declarations: [
    MyReviewComponent,
  ],
  exports: [
    MyReviewComponent,
  ],
  providers: [
    ReviewService,
    DatePipe
  ]
})

export class MyReviewModule {
}
