import { NgModule } from '@angular/core';

import { NumberHalfRoundPipe } from './number-half-round.pipe';

@NgModule({
  declarations: [
    NumberHalfRoundPipe,
  ],
  exports: [
    NumberHalfRoundPipe
  ]
})
export class NumberHalfRoundPipeModule { }
