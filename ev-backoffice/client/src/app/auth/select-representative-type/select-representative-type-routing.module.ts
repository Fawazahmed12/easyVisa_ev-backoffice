import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SelectRepresentativeTypeComponent } from './select-representative-type.component';

export const routes: Routes = [
  { path: '', component: SelectRepresentativeTypeComponent, },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class SelectRepresentativeTypeRoutingModule {
}
