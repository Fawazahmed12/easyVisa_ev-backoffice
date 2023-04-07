import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { TaxesInfoComponent } from './taxes-info.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    TaxesInfoComponent,
  ],
  exports: [
    TaxesInfoComponent,
  ],
})
export class TaxesInfoModule { }
