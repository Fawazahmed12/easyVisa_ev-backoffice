import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';

import { FindLabelPipe } from '../../../shared/pipes/find-label/find-label.pipe';
import { SharedModule } from '../../../shared/shared.module';
import { TableModule } from '../../../components/table/table.module';

import { PermissionsService } from '../permissions.service';
import { PermissionsLevelModalModule } from '../modals/permissions-level-modal/permissions-level-modal.module';

import { PermissionsTableComponent } from './permissions-table.component';
import { PermissionsTableRoutingModule } from './permissions-table-routing.module';

@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    NgbModalModule,
    PermissionsTableRoutingModule,
    PermissionsLevelModalModule,
    TableModule,
  ],
  declarations: [
    PermissionsTableComponent,
  ],
  exports: [
    PermissionsTableComponent,
  ],
  providers: [
    PermissionsService,
    DatePipe,
    FindLabelPipe
  ]
})
export class PermissionsTableModule { }
