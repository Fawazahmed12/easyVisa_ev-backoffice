import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';
import { SelectNameModule } from '../../../../components/select-name/select-name.module';
import { RepresentativeTypePipeModule } from '../../../../shared/pipes/representative-type/representative-type-pipe.module';

import { AssignToComponent } from './assign-to.component';

@NgModule({
  imports: [
    SharedModule,
    SelectNameModule,
    RepresentativeTypePipeModule,
  ],
  declarations: [
    AssignToComponent,
  ],
  exports: [
    AssignToComponent,
  ]
})
export class AssignToModule {
}
