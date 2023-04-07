import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SpinnerModule } from '../components/spinner/spinner.module';

import { IndexComponent } from './index.component';
import { IndexRoutingModule } from './index-routing.module';


@NgModule({
  imports: [
    CommonModule,
    IndexRoutingModule,
    SpinnerModule,
  ],
  declarations: [
    IndexComponent,
  ],
  exports: [
    IndexComponent,
  ]
})
export class IndexModule { }
