import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SuperAdminRoutingModule } from './super-admin-routing.module';
import { SuperAdminComponent } from './super-admin.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { NgbDropdownConfig, NgbDropdownModule, NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { NgSelectModule } from "@ng-select/ng-select";



@NgModule({
  declarations: [
    SuperAdminComponent
  ],
  imports: [
    CommonModule,
    SuperAdminRoutingModule,
    ReactiveFormsModule,
    TranslateModule,
    NgbDropdownModule,
    FormsModule,
    NgbTypeaheadModule,
    NgSelectModule
  ],
  providers: [NgbDropdownConfig],
})
export class SuperAdminModule { }
