import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { PaginationComponent } from './pagination.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    NgbPaginationModule,
  ],
  declarations: [
    PaginationComponent,
  ],
  exports: [
    PaginationComponent,
  ],
})
export class PaginationModule { }
