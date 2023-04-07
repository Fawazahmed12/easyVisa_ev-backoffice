import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { IndividualClientSearchComponent } from './individual-client-search.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    IndividualClientSearchComponent,
  ],
  exports: [
    IndividualClientSearchComponent,
  ]
})

export class IndividualClientSearchModule {
}
