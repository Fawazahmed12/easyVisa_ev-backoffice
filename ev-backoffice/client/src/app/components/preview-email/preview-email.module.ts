import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SafeHtmlPipeModule } from '../../shared/pipes/safe-html/safeHtmlPipe.module';

import { SpinnerModule } from '../spinner/spinner.module';

import { PreviewEmailComponent } from './preview-email.component';


@NgModule({
  imports: [
    CommonModule,
    SpinnerModule,
    SafeHtmlPipeModule
  ],
  declarations: [
    PreviewEmailComponent,
  ],
  exports: [
    PreviewEmailComponent,
  ]
})
export class PreviewEmailModule {

}
