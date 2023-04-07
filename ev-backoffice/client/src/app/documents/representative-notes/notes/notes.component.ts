import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Note } from '../../models/note.model';


@Component({
  selector: 'app-notes',
  templateUrl: './notes.component.html',
})

export class NotesComponent {
  @Input() notes: Note[];
  @Input() disableRemove: boolean;
  @Input() readOnlyAccess: boolean;

  @Output() removeNoteRow = new EventEmitter();

  removeNote(index) {
    this.removeNoteRow.emit(index);
  }
}

