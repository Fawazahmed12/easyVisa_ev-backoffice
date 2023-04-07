import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { NgbPaginationModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../../shared/shared.module';
import { SelectNameModule } from '../../../components/select-name/select-name.module';
import { TableModule } from '../../../components/table/table.module';
import {
  RepresentativeTypePipeModule
} from '../../../shared/pipes/representative-type/representative-type-pipe.module';
import {
  SelectRepresentativeHeaderModule
} from '../../../components/select-representative-header/select-representative-header.module';
import { TimePeriodModule } from '../../../components/time-period/time-period.module';

import { PackagesRoutingModule } from './packages-routing.module';
import { PackagesComponent } from './packages.component';
import { MODALS } from './modals';
import { PackagesResolverService } from './packages-resolver.service';
import { FindLabelPipeModule } from '../../../shared/pipes/find-label/find-label-pipe.module';
import { SpinnerModule } from '../../../components/spinner/spinner.module';
import {
  AccessDeniedForTraineeModalComponent
} from './modals/access-denied-for-trainee-modal/access-denied-for-trainee-modal.component';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    PackagesRoutingModule,
    SelectNameModule,
    TableModule,
    NgbPaginationModule,
    MODALS,
    RepresentativeTypePipeModule,
    SelectRepresentativeHeaderModule,
    TimePeriodModule,
    NgbTooltipModule,
    SpinnerModule,
    ModalHeaderModule,
  ],
  declarations: [
    PackagesComponent,
    AccessDeniedForTraineeModalComponent,
  ],
  providers: [
    PackagesResolverService,
    FindLabelPipeModule,
    DatePipe,
  ]
})
export class PackagesModule {
}
