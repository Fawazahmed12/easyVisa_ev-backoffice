import { NgModule } from '@angular/core';
import { HasRoleDirective } from './has-role.directive';


@NgModule({
  declarations: [
    HasRoleDirective,
  ],
  exports: [
    HasRoleDirective,
  ]
})
export class HasRoleDirectiveModule {
}
