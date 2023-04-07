import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { LanguageSelectModule } from '../language-select/language-select.module';
import { SelectOrganizationRepresentativeModule } from '../select-organization-representative/select-organization-representative.module';

import { EntryComponent } from './entry.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    LanguageSelectModule,
    SelectOrganizationRepresentativeModule,
  ],
  declarations: [
    EntryComponent,
  ],
  exports: [
    EntryComponent,
  ]
})
export class EntryModule { }
