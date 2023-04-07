import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { FileIconComponent } from './file-icon.component';
import { FileIconPipeModule } from './pipes/file-icon-pipe.module';

@NgModule({
  imports: [
    SharedModule,
    FileIconPipeModule,
  ],
  declarations: [
    FileIconComponent,
  ],
  exports: [
    FileIconComponent,
  ],
})
export class FileIconModule {
}
