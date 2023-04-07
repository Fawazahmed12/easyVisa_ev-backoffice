import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { DeleteMyDataComponent } from './delete-my-data.component';
import { PermanentlyDeleteModalModule } from './permanently-delete-modal/permanently-delete-modal.module';

@NgModule({
  imports: [
    SharedModule,
    PermanentlyDeleteModalModule
  ],
  declarations: [
    DeleteMyDataComponent,
  ],
  exports: [
    DeleteMyDataComponent,
  ]
})

export class DeleteMyDataModule {
}
