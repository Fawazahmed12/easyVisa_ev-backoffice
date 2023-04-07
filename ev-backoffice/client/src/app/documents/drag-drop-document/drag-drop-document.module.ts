import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NgxFileDropModule } from 'ngx-file-drop';

import { SharedModule } from '../../shared/shared.module';

import { DragDropDocumentComponent } from './drag-drop-document.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    NgxFileDropModule
  ],
  declarations: [ DragDropDocumentComponent ],
  exports: [ DragDropDocumentComponent ]
})
export class DragDropDocumentModule {
}
