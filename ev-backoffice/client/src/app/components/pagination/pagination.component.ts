import { Component, EventEmitter, Input, Output } from '@angular/core';


@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
})

export class PaginationComponent {
  @Input() collectionSize: string | number;
  @Input() pageSize: string | number;
  @Input() page: number;
  @Input() rotate = true;
  @Input() maxSize = 5;
  @Input() boundaryLinks = true;
  @Input() noOffset = false;
  @Output() pageChange = new EventEmitter();

  paginationPageChange(page) {
    this.pageChange.emit(page);
  }
}
