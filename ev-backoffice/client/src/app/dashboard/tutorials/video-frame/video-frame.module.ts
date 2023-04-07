import { NgModule } from '@angular/core';
import { VideoFrameComponent } from './video-frame.component';
import { SharedModule } from '../../../shared/shared.module';
import { BlockModule } from '../../../shared/components/block/block.module';


@NgModule({
  imports: [SharedModule, BlockModule],
  declarations: [VideoFrameComponent],
  exports: [
    VideoFrameComponent
  ]
})
export class VideoFrameModule {
}
