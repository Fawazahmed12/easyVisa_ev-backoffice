import { DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { TableModule } from '../../components/table/table.module';
import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';
import { SafeUrlPipeModule } from '../../shared/pipes/safe-url/safeUrlPipe.module';
import { SpinnerModule } from '../../components/spinner/spinner.module';
import { PaginationModule } from '../../components/pagination/pagination.module';

import { DispositionsResolverService } from './dispositions-resolver.service';
import { DispositionsRoutingModule } from './dispositions-routing.module';
import { DispositionsComponent } from './dispositions.component';
import { DispositionsService } from './dispositions.service';
import { RejectFileModule } from './reject-file/reject-file.module';
import { EnlargeDocumentModule } from './enlarge-document/enlarge-document.module';


@NgModule({
  imports: [
    SharedModule,
    DispositionsRoutingModule,
    TableModule,
    RepresentativeTypePipeModule,
    RejectFileModule,
    EnlargeDocumentModule,
    SafeUrlPipeModule,
    SpinnerModule,
    PaginationModule
  ],
  declarations: [
    DispositionsComponent,
  ],
  providers: [
    DispositionsResolverService,
    DispositionsService,
    DatePipe,
  ],
})
export class DispositionsModule {
}
