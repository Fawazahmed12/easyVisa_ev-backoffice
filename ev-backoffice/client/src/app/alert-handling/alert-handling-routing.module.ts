import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AlertReplyComponent } from './alert-reply/alert-reply.component';


export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: ':alertId/reply',
        component: AlertReplyComponent,
      },
    ],
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
export class AlertHandlingRoutingModule {
}
