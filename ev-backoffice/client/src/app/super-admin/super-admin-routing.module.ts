import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SuperAdminComponent } from './super-admin.component';

export const routes: Routes = [
  {
    path:'',
    component:SuperAdminComponent,
    data:{
    translationUrl:'super-admin-module'
  }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SuperAdminRoutingModule { }
