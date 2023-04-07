import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { FileInfo } from '../../core/models/file-info.model';

@Component({
  selector: 'app-file-icon',
  templateUrl: './file-icon.component.html',
  styleUrls: ['./file-icon.component.scss']
})

export class FileIconComponent {
  @Input() fileInfo: FileInfo = null;
  @Input() showRemoveIcon = false;
  @Output() removeDocument: EventEmitter<FileInfo> = new EventEmitter();

  deleteDocument() {
    this.removeDocument.emit(this.fileInfo);
  }
}
