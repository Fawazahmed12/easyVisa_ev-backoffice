import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { SelectRepresentativeTypeRoutingModule } from './select-representative-type-routing.module';
import { SelectRepresentativeTypeComponent } from './select-representative-type.component';
import { ProfileNotLinkedModalComponent } from './profile-not-linked-modal/profile-not-linked-modal.component';

@NgModule({
  imports: [
    SharedModule,
    SelectRepresentativeTypeRoutingModule,
  ],
  declarations: [
    SelectRepresentativeTypeComponent,
    ProfileNotLinkedModalComponent,
  ],
})
export class SelectRepresentativeTypeModule {
}
