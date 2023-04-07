import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UscisEditionDatesComponent } from './uscis-edition-dates.component';
import { UscisEditionDatesResolverService } from './uscis-edition-dates-resolver.service';

const routes: Routes = [
  {
    path: '',
    component: UscisEditionDatesComponent,
    resolve: [
      UscisEditionDatesResolverService,
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UscisEditionDatesRoutingModule {
}
