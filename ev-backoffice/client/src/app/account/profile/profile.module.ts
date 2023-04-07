import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { ProfileRoutingModule } from './profile-routing.module';
import { EditPreviewProfileModule } from './edit-preview-profile/edit-preview-profile.module';
import { RequestJoinPageModule } from './request-join-page/request-join-page.module';
import { ImageCropperModule } from 'ngx-image-cropper';

@NgModule({
  imports: [
    SharedModule,
    ProfileRoutingModule,
    EditPreviewProfileModule,
    RequestJoinPageModule,
    ImageCropperModule,
  ],
})
export class ProfileModule {
}
