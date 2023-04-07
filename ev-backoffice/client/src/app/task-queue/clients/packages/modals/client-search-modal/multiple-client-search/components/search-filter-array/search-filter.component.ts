import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-search-filter',
  templateUrl: './search-filter.component.html',
})

export class SearchFilterComponent {
  @Input() array: any[];
  @Input() controlName: string;
  @Input() controlValue: any[] | null;
  @Input() searchLabel = false;
  @Output() removedItem = new EventEmitter();

  removeItem(item) {
    this.removedItem.emit(item);
  }
}
