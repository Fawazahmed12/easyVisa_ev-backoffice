import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SignUpResolverService } from './sign-up-resolver.service';
import { SignUpComponent } from './sign-up.component';

const routes: Routes = [
  {
    path: '',
    component: SignUpComponent,
    resolve: [
      SignUpResolverService,
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SignUpRoutingModule { }
