import { NgModule } from '@angular/core';

import { NgbProgressbarModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../../shared/shared.module';

import { ProgressUnitComponent } from './progress-unit.component';


@NgModule({
  imports: [
    SharedModule,
    NgbProgressbarModule
  ],
  declarations: [
    ProgressUnitComponent,
  ],
  exports: [
    ProgressUnitComponent,
  ],
})
export class ProgressUnitModule {
}
