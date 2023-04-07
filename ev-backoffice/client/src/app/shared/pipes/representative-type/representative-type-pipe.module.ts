import { NgModule } from '@angular/core';

import { RepresentativeTypePipe } from './representative-type.pipe';

@NgModule({
  declarations: [
    RepresentativeTypePipe,
  ],
  exports: [
    RepresentativeTypePipe
  ]
})
export class RepresentativeTypePipeModule { }
