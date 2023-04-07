import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../../../shared/shared.module';

import { UscisEditionDatesEditorComponent } from './uscis-edition-dates-editor.component';
import { UscisEditionDatesRowComponent } from './uscis-edition-dates-row/uscis-edition-dates-row.component';
import { DatepickerGroupModule } from '../../../../components/datepicker-group/datepicker-group.module';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    DatepickerGroupModule
  ],
  declarations: [UscisEditionDatesEditorComponent, UscisEditionDatesRowComponent],
  exports: [UscisEditionDatesEditorComponent]
})
export class UscisEditionDatesEditorModule { }
