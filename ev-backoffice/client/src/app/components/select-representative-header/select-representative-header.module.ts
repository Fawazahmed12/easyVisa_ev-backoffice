import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';
import { SelectNameModule } from '../select-name/select-name.module';
import { SelectRepresentativeHeaderComponent } from './select-representative-header.component';

@NgModule({
  imports: [
    SharedModule,
    SelectNameModule,
    RepresentativeTypePipeModule
  ],
  declarations: [
    SelectRepresentativeHeaderComponent,
  ],
  exports: [
    SelectRepresentativeHeaderComponent,
  ],
})
export class SelectRepresentativeHeaderModule { }
