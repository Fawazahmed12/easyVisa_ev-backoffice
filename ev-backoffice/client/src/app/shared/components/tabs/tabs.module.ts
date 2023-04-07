import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';

import { TabsComponent } from './tabs.component';

@NgModule({
  imports: [
    CommonModule,
    TranslateModule,
    RouterModule,
  ],
  declarations: [
    TabsComponent,
  ],
  exports: [
    TabsComponent,
  ]
})
export class TabsModule {
}
