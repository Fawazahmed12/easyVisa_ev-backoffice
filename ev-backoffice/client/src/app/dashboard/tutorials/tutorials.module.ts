import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { BlockModule } from '../../shared/components/block/block.module';

import { TutorialsRoutingModule } from './tutorials-routing.module';
import { TutorialsComponent } from './tutorials.component';
import { VideoFrameModule } from './video-frame/video-frame.module';


@NgModule({
  imports: [
    SharedModule,
    TutorialsRoutingModule,
    VideoFrameModule,
    BlockModule,
  ],
  declarations: [TutorialsComponent],
})
export class TutorialsModule {
}
