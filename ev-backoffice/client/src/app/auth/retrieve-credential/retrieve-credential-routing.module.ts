import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RetrieveCredentialComponent } from './retrieve-credential.component';

const routes: Routes = [
  { path: '', component: RetrieveCredentialComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RetrieveCredentialRoutingModule { }
