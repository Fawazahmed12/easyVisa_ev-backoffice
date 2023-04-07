import { NgModule } from '@angular/core';

import { SafeUrlPipe } from './safeUrl.pipe';

@NgModule({
  declarations: [
    SafeUrlPipe,
  ],
  exports: [
    SafeUrlPipe
  ]
})
export class SafeUrlPipeModule { }
