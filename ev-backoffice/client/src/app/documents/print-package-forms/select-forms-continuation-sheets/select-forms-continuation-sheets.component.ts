import { Component, EventEmitter, forwardRef, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Subject } from "rxjs";


@Component({
  selector: 'app-select-forms-continuation-sheets',
  templateUrl: './select-forms-continuation-sheets.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectFormsContinuationSheetsComponent),
      multi: true
    }
  ]
})

export class SelectFormsContinuationSheetsComponent implements ControlValueAccessor, OnInit, OnDestroy {
  @Input()
  continuationSheets = null;
  @Input() forms = null;
  @Input() type: String;
  @Output() isCheck = new EventEmitter();
  @Input() selectAllEvent = new Subject<any>();
  @Output() checkBoxChangeEvent = new EventEmitter<any>()

  ids = [];
  disabled = false;

  onChange: (value: string[]) => void;
  onTouched: () => void;

  onCheckChange(event, checkValue) {
    this.isCheck.emit(event.target.checked);
    const isValue = this.ids.includes(checkValue);
    if (isValue) {
      this.ids = this.ids.filter((item) => item !== checkValue);
    } else {
      this.ids = [...this.ids, checkValue];
    }
    this.checkBoxChangeEvent.emit({ 'checked': this.isCheckedAll(), type: this.type })
    this.onChange(this.ids);
  }

  ngOnInit() {
    this.selectAllEvent.subscribe(({ isChecked, type }) => {
      if (type == 'forms' && this.forms?.length) {
        this.ids = this.getIdByChecked(isChecked, this.forms, 'formId')
      } else if (type == 'continuationSheets' && this.continuationSheets?.length) {
        this.ids = this.getIdByChecked(isChecked, this.continuationSheets, 'continuationSheetId')
      }
      this.onChange(this.ids);
    })
  }

  writeValue(value) {
    this.ids = [...value];
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    isDisabled ? this.disabled = true : this.disabled = false;
  }

  getContinuationSheetDisplayName(item) {
    return `[Item ${item.continuationSheetItem}] - ${item.continuationSheetName}`;
  }

  ngOnDestroy(): void {
    this.selectAllEvent.unsubscribe();
  }

  private isCheckedAll(): boolean {
    return this.canCheckedAll('forms', this.forms) || this.canCheckedAll('continuationSheets', this.continuationSheets)
  }

  private canCheckedAll(formType: string, formsOrContinuationSheets): boolean {
    return this.type == formType && formsOrContinuationSheets?.length && formsOrContinuationSheets.length == this.ids.length
  }

  private getIdByChecked(isChecked, formsOrContinuationSheets, idType): string[] {
    const formOrContinuationSheetIds = formsOrContinuationSheets.map(item => item[ idType ]);
    if (isChecked) {
      return [...new Set([...this.ids, ...formOrContinuationSheetIds])]
    } else {
      return [...this.ids].filter(id => !formOrContinuationSheetIds.includes(id))
    }
  }
}
