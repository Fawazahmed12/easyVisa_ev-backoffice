import { Component, Input, OnInit } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';

import { NoteTypes } from '../../models/note-types.enum';


@Component({
  selector: 'app-add-new-note',
  templateUrl: './add-new-note.component.html',
})

export class AddNewNoteComponent implements OnInit {
  @Input() type;
  title$: Observable<string>;
  str = '';

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  ngOnInit() {
    this.title$ = of(this.type === NoteTypes.PUBLIC_NOTE ?
      'TEMPLATE.REPRESENTATIVE_NOTES.NEW_PUBLIC_NOTE_TITLE' : 'TEMPLATE.REPRESENTATIVE_NOTES.NEW_REPRESENTATIVE_NOTE_TITLE');
  }

  saveNote(data) {
    this.activeModal.close({
      subject: data,
      type: this.type
    });
  }

  closeModal() {
    this.activeModal.dismiss();
  }
}

