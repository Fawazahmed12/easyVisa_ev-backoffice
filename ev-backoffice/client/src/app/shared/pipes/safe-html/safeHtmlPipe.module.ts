import { NgModule } from '@angular/core';

import { SafeHtmlPipe } from './safeHtml.pipe';

@NgModule({
  declarations: [
    SafeHtmlPipe,
  ],
  exports: [
    SafeHtmlPipe
  ]
})
export class SafeHtmlPipeModule { }
