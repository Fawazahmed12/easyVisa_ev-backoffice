import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import * as FileSaver from 'file-saver';

import { DocumentFileType, DocumentFileTypeIcons } from '../models/documents.model';

@Component({
  selector: 'app-document-viewer-modal',
  templateUrl: './document-viewer-modal.component.html',
  styleUrls: [ './document-viewer-modal.component.scss' ]
})

export class DocumentViewerModalComponent implements OnInit {

  @Input() documentFileData;
  @Input() readOnlyAccess;

  imageFileTypes = [
    DocumentFileType.bmp,
    DocumentFileType.bmp1,
    DocumentFileType.jpeg,
    DocumentFileType.jpeg1,
    DocumentFileType.jpg,
    DocumentFileType.jpg1,
    DocumentFileType.png
  ];

  constructor(public activeModal: NgbActiveModal) {
  }

  ngOnInit() {
  }

  onDocumentAttachmentDownload() {
    FileSaver.saveAs(this.documentFileData.fileURL, this.documentFileData.fileName);
  }

  hasImageSourceByFileType(fileContentType) {
    return this.imageFileTypes.indexOf(fileContentType) !== -1;
  }

  hasPdfFileType(fileContentType) {
    return fileContentType === DocumentFileType.pdf;
  }

  getFileTypeIcon(fileType) {
    return DocumentFileTypeIcons[ fileType ];
  }

}
