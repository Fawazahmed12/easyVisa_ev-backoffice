import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LanguageSelectComponent } from './language-select.component';

@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    LanguageSelectComponent,
  ],
  exports: [
    LanguageSelectComponent,
  ]
})
export class LanguageSelectModule { }
