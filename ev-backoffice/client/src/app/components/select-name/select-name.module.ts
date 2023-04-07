import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';
import { SelectNameComponent } from './select-name.component';

@NgModule({
  imports: [
    SharedModule,
    RepresentativeTypePipeModule,
  ],
  declarations: [
    SelectNameComponent,
  ],
  exports: [
    SelectNameComponent,
  ],
})
export class SelectNameModule { }
