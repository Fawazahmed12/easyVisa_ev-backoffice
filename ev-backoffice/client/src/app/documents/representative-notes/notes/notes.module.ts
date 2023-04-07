import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { NotesComponent } from './notes.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    NotesComponent,
  ],
  exports: [
    NotesComponent,
  ],
  entryComponents: [
    NotesComponent,
  ]
})
export class NotesModule {
}
