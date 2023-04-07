import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { LogoMessageComponent } from './logo-message.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [
    LogoMessageComponent,
  ],
  entryComponents: [
    LogoMessageComponent,
  ],
  exports: [
    LogoMessageComponent
  ]
})
export class LogoMessageModule {
}
