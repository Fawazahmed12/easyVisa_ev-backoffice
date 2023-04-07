import { NgModule } from '@angular/core';
import { DatePipe } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { SelectPackageModule } from '../../components/select-package/select-package.module';
import { MyPackagesResolverService } from '../../core/resolvers/my-packages-resolver.service';
import { StarsRatingModule } from '../../components/stars-rating/stars-rating.module';
import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';
import { SpinnerModule } from '../../components/spinner/spinner.module';

import { ReviewService } from '../services/review.service';

import { WriteReviewRoutingModule } from './write-review-routing.module';
import { WriteReviewComponent } from './write-review.component';
import { FindLabelPipeModule } from '../../shared/pipes/find-label/find-label-pipe.module';


@NgModule({
  imports: [
    SharedModule,
    WriteReviewRoutingModule,
    SelectPackageModule,
    StarsRatingModule,
    RepresentativeTypePipeModule,
    SpinnerModule,
    FindLabelPipeModule
  ],
  declarations: [
    WriteReviewComponent,
  ],
  exports: [
    WriteReviewComponent,
  ],
  providers: [
    MyPackagesResolverService,
    ReviewService,
    DatePipe
  ],
})
export class WriteReviewModule {
}
