import { Component, Input } from '@angular/core';

import { VideoConfig } from '../../models/video-config.model';


@Component({
  selector: 'app-video-frame',
  templateUrl: './video-frame.component.html',
})

export class VideoFrameComponent {
  @Input() videoConfig: VideoConfig;
}
