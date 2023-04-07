import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { BlockModule } from '../../shared/components/block/block.module';
import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';
import { OrganizationTypeModule } from '../../shared/pipes/organization-type/organization-type.module';

import { RepresentativeNotesComponent } from './representative-notes.component';
import { AddNewNoteModule } from './add-new-note/add-new-note.module';
import { NotesModule } from './notes/notes.module';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
    RepresentativeTypePipeModule,
    AddNewNoteModule,
    OrganizationTypeModule,
    NotesModule,
  ],
  declarations: [
    RepresentativeNotesComponent,
  ],
  exports: [
    RepresentativeNotesComponent,
  ],
})
export class RepresentativeNotesModule {
}
