import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RepresentativeMessagePageComponent } from './representative-message-page.component';


export const routes: Routes = [
  {
    path: '',
    component: RepresentativeMessagePageComponent,
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
export class RepresentativeMessagePageRoutingModule {
}
