import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { TableModule } from '../../components/table/table.module';
import { SelectRepresentativeHeaderModule } from '../../components/select-representative-header/select-representative-header.module';
import { SpinnerModule } from '../../components/spinner/spinner.module';
import { PaginationModule } from '../../components/pagination/pagination.module';

import { WarningsRoutingModule } from './warnings-routing.module';
import { WarningsService } from './warnings.service';
import { WarningsComponent } from './warnings.component';
import { WarningsResolverService } from './warnings-resolver.service';


@NgModule({
  imports: [
    CommonModule,
    TableModule,
    SharedModule,
    SelectRepresentativeHeaderModule,
    WarningsRoutingModule,
    SpinnerModule,
    PaginationModule,
  ],
  declarations: [
    WarningsComponent,
  ],
  providers: [
    DatePipe,
    WarningsService,
    WarningsResolverService,
  ],
})
export class WarningsModule { }
