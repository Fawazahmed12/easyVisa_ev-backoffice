import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { UploadPhotoComponent } from './upload-photo.component';
import { ConfirmModalModule } from '../../../../../../core/modals/confirm-modal/confirm-modal.module';
import { PhotoCropperModule } from '../../../../../../components/photo-cropper/photo-cropper.module';

@NgModule({
  imports: [
    SharedModule,
    ConfirmModalModule,
    PhotoCropperModule
  ],
  declarations: [
    UploadPhotoComponent,
  ],
  exports: [
    UploadPhotoComponent,
  ]
})

export class UploadPhotoModule {
}
