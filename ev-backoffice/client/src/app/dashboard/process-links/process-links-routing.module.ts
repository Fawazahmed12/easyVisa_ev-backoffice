import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ProcessLinksComponent } from './process-links.component';


const routes: Routes = [
  {
    path: '',
    component: ProcessLinksComponent,
  },
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class ProcessLinksRoutingModule {
}
