import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { ImageCropperModule } from 'ngx-image-cropper';

import { SharedModule } from '../../shared/shared.module';

import { AuthFormFieldModule } from '../auth-form-field/auth-form-field.module';
import { ModalHeaderModule } from '../modal-header/modal-header.module';

import { PhotoCropperComponent } from './photo-cropper.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    AuthFormFieldModule,
    NgbTooltipModule,
    ImageCropperModule,
    ModalHeaderModule,
  ],
  declarations: [
    PhotoCropperComponent,
  ],
  exports: [
    PhotoCropperComponent,
  ]
})
export class PhotoCropperModule { }
