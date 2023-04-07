import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './home.component';
import { I18nResolverService } from '../core/i18n/i18n-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    resolve: {
      translation: I18nResolverService,
    },
    data: {
      translationUrl: 'home-module',
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HomeRoutingModule {
}
