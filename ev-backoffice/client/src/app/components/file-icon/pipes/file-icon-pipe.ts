import { Pipe, PipeTransform } from '@angular/core';
import { FileType } from '../../../core/models/file-type.enum';

@Pipe({name: 'fileIcon'})
export class FileIconPipe implements PipeTransform {
  transform(value: FileType) {
    switch (value) {
      case FileType.TXT:
      case FileType.RTF: {
        return 'fa-file-text-o';
      }
      case FileType.DOCX:
      case FileType.DOC: {
        return 'fa-file-word-o';
      }
      case FileType.PDF: {
        return 'fa-file-pdf-o';
      }
    }
  }
}
