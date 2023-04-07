import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { PackageComponent } from './package.component';
import { PackageRoutingModule } from './package-routing.module';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    PackageRoutingModule,
  ],
  declarations: [
    PackageComponent,
  ],
})
export class PackageModule { }
