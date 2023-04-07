import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { ReviewService } from '../../../../../../services/review.service';
import { BlockModule } from '../../../../../../../shared/components/block/block.module';
import { StarsRatingModule } from '../../../../../../../components/stars-rating/stars-rating.module';

import { WriteReplyComponent } from './write-reply.component';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
    StarsRatingModule
  ],
  declarations: [
    WriteReplyComponent
  ],
  exports: [
    WriteReplyComponent
  ],
  providers: [
    ReviewService,
  ]
})

export class WriteReviewModule {
}
