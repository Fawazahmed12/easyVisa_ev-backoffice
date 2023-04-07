import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { TableHeader } from './models/table-header.model';


@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
@DestroySubscribers()
export class TableComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() header: TableHeader[] = [];
  @Input() overHeader: TableHeader[] = [];
  @Input() stickyTable = true;
  @Input() selectionStickyTable = false;
  @Input() tableData: any[] = [];
  @Input() tableSortCol: string = null;
  @Input() tableSortOrder: string = null;
  @Input() scrolledTableBody = false;
  @Input() permissionsTableBody = false;
  @Input() selectedItemsFormControl: FormControl = new FormControl([]);
  @Input() disableSortingStarColumn: number;
  @Input() borderNone: boolean;
  @Input() smallCells: boolean;
  @Input() hideScrollBar = true;
  @Input() noDataText = 'TEMPLATE.TABLES.NO_DATA';
  @Input() noDataCustomShowing = false;
  @Input() packagesTable = false;

  @Output() sortParams = new EventEmitter();
  @Output() setAdmin = new EventEmitter();
  @Output() actionItem = new EventEmitter();
  @Output() rowClick = new EventEmitter();

  selectAllFormControl: FormControl = new FormControl(false);
  private subscribers: any = {};
  result: TableHeader[];


  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
    this.result = [...this.header, ...this.overHeader];
  }

  addSubscribers() {
    this.subscribers.selectAllSubscription = this.selectAllFormControl.valueChanges.subscribe(
      (value) => {
        const selectedIds = this.tableData.map((item) => {
          item.checkbox.patchValue(value);
          return item.id;
        });
        this.selectedItemsFormControl.patchValue(value ? selectedIds : []);
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  sortByHeaderCol(colName) {
    this.sortParams.emit(colName);
  }

  openModal(item) {
    this.actionItem.emit(item);
  }

  updateCheckedItems(item) {
    let selectedIds = [];
    if (item.checkbox.value) {
      selectedIds = this.selectedItemsFormControl.value.filter((id) => id !== item.id);
    } else {
      selectedIds = [...this.selectedItemsFormControl.value, item.id];
    }
    this.selectedItemsFormControl.patchValue(selectedIds);
    this.selectAllFormControl.patchValue(selectedIds.length === this.tableData.length, {emitEvent: false});
  }

  onRowClick(item) {
    this.rowClick.emit(item);
  }

  updateAdminCheckedItems(item) {
    return this.setAdmin.emit(item.id);
  }
}
