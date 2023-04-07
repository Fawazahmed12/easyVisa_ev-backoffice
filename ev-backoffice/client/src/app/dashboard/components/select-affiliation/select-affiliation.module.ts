import { NgModule } from '@angular/core';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../../shared/shared.module';

import { SelectAffiliationComponent } from './select-affiliation.component';


@NgModule({
  imports: [
    SharedModule,
    NgbTooltipModule
  ],
  declarations: [
    SelectAffiliationComponent,
  ],
  exports: [
    SelectAffiliationComponent,
  ],
})
export class SelectAffiliationModule {
}
