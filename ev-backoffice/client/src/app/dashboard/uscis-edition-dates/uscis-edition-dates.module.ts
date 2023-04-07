import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { UscisEditionDatesRoutingModule } from './uscis-edition-dates-routing.module';
import { UscisEditionDatesComponent } from './uscis-edition-dates.component';
import { TableModule } from '../../components/table/table.module';
import { UscisEditionDatesResolverService } from './uscis-edition-dates-resolver.service';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    UscisEditionDatesRoutingModule,
    TableModule
  ],
  declarations: [UscisEditionDatesComponent],
  providers: [
    UscisEditionDatesResolverService,
  ],
})
export class UscisEditionDatesModule {
}
