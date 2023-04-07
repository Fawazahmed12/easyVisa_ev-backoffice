import { Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

import { filter } from 'rxjs/operators';

import { DestroySubscribers } from 'ngx-destroy-subscribers';

@Component({
  selector: 'app-stars-rating',
  templateUrl: './stars-rating.component.html',
  styleUrls: ['./stars-rating.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => StarsRatingComponent),
      multi: true
    }
  ]
})
@DestroySubscribers()
export class StarsRatingComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() max = 5;
  @Input() rate: any;
  @Input() readonly = false;

  formControl = new FormControl();

  onChange: (value: string) => void;
  onTouched: () => void;

  private subscribers: any = {};

  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
  }

  addSubscribers() {
    this.subscribers.organizationIdSubscription = this.formControl.valueChanges.pipe(
      filter(() => !!this.onChange)
    ).subscribe((value) => this.onChange(value));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  writeValue(value) {
    this.formControl.patchValue(value, {emitEvent: false});
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    isDisabled ?
      this.formControl.disable({emitEvent: false})
      :
      this.formControl.enable({emitEvent: false});
  }
}
