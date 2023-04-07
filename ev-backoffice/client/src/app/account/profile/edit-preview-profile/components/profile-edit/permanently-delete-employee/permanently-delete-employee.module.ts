import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { PermanentlyDeleteEmployeeComponent } from './permanently-delete-employee.component';
import { DeleteEmployeeModalModule } from './delete-employee-modal/delete-employee-modal.module';


@NgModule({
  imports: [
    SharedModule,
    DeleteEmployeeModalModule,
  ],
  declarations: [
    PermanentlyDeleteEmployeeComponent,
  ],
  exports: [
    PermanentlyDeleteEmployeeComponent,
  ]
})

export class PermanentlyDeleteEmployeeModule {
}
