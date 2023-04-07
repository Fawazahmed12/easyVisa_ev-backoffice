import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SpinnerModule } from '../components/spinner/spinner.module';

import { RedirectEmployeeComponent } from './redirect-employee.component';
import { RedirectEmployeeRoutingModule } from './redirect-employee-routing.module';


@NgModule({
  imports: [
    SharedModule,
    SpinnerModule,
    RedirectEmployeeRoutingModule,
  ],
  declarations: [
    RedirectEmployeeComponent,
  ]
})
export class RedirectEmployeeModule { }
