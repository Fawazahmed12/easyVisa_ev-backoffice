import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { PermissionsTableComponent } from './permissions-table.component';

const routes: Routes = [
  {
    path: '',
    component: PermissionsTableComponent,
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class PermissionsTableRoutingModule {
}
