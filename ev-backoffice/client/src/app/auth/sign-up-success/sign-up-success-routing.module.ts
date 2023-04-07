import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SignUpSuccessComponent } from './sign-up-success.component';


export const routes: Routes = [
  {
    path: '',
    component: SignUpSuccessComponent,
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class SignUpSuccessRoutingModule {
}
