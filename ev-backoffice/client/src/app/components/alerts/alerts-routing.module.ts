import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AlertsResolverService } from '../../core/resolvers/alerts-resolver.service';

import { AlertsComponent } from './alerts.component';

export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        component: AlertsComponent,
        resolve: [
          AlertsResolverService,
        ],
      }
    ]
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
export class AlertsRoutingModule {
}
