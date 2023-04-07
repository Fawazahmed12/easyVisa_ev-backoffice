import { NgModule } from '@angular/core';

import { FindLabelPipe } from './find-label.pipe';

@NgModule({
  declarations: [
    FindLabelPipe,
  ],
  exports: [
    FindLabelPipe
  ]
})
export class FindLabelPipeModule { }
