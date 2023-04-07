import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UploadDocumentComponent } from './upload-document.component';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [
    UploadDocumentComponent
  ],
  exports: [
    UploadDocumentComponent
  ]
})
export class UploadDocumentModule {
}
