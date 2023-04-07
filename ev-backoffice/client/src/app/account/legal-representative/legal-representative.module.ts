import { NgModule } from '@angular/core';
import { DatePipe } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { SelectPackageModule } from '../../components/select-package/select-package.module';
import { MyPackagesResolverService } from '../../core/resolvers/my-packages-resolver.service';
import { SpinnerModule } from '../../components/spinner/spinner.module';
import { FindLabelPipeModule } from '../../shared/pipes/find-label/find-label-pipe.module';

import { LegalRepresentativeRoutingModule } from './legal-representative-routing.module';
import { LegalRepresentativeComponent } from './legal-representative.component';


@NgModule({
  imports: [
    SharedModule,
    LegalRepresentativeRoutingModule,
    SelectPackageModule,
    SpinnerModule,
    FindLabelPipeModule
  ],
  declarations: [
    LegalRepresentativeComponent,
  ],
  exports: [
    LegalRepresentativeComponent,
  ],
  providers: [
    MyPackagesResolverService,
    DatePipe
  ],
})
export class LegalRepresentativeModule {
}
