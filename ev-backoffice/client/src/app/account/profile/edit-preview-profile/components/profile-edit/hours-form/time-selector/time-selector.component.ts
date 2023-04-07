import { Component, forwardRef } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-time-selector',
  templateUrl: './time-selector.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TimeSelectorComponent),
      multi: true
    }
  ]
})

export class TimeSelectorComponent implements ControlValueAccessor {
  selectedTimeFormControl;

  workingTime = [
    {hour: null, minutes: null, value: 'Closed'},
    {hour: 0, minutes: 0, value: '0:00 AM'},
    {hour: 0, minutes: 30, value: '0:30 AM'},
    {hour: 1, minutes: 0, value: '1:00 AM'},
    {hour: 1, minutes: 30, value: '1:30 AM'},
    {hour: 2, minutes: 0, value: '2:00 AM'},
    {hour: 2, minutes: 30, value: '2:30 AM'},
    {hour: 3, minutes: 0, value: '3:00 AM'},
    {hour: 3, minutes: 30, value: '3:30 AM'},
    {hour: 4, minutes: 0, value: '4:00 AM'},
    {hour: 4, minutes: 30, value: '4:30 AM'},
    {hour: 5, minutes: 0, value: '5:00 AM'},
    {hour: 5, minutes: 30, value: '5:30 AM'},
    {hour: 6, minutes: 0, value: '6:00 AM'},
    {hour: 6, minutes: 30, value: '6:30 AM'},
    {hour: 7, minutes: 0, value: '7:00 AM'},
    {hour: 7, minutes: 30, value: '7:30 AM'},
    {hour: 8, minutes: 0, value: '8:00 AM'},
    {hour: 8, minutes: 30, value: '8:30 AM'},
    {hour: 9, minutes: 0, value: '9:00 AM'},
    {hour: 9, minutes: 30, value: '9:30 AM'},
    {hour: 10, minutes: 0, value: '10:00 AM'},
    {hour: 10, minutes: 30, value: '10:30 AM'},
    {hour: 11, minutes: 0, value: '11:00 AM'},
    {hour: 11, minutes: 30, value: '11:30 AM'},
    {hour: 12, minutes: 0, value: '12:00 PM'},
    {hour: 12, minutes: 30, value: '12:30 PM'},
    {hour: 13, minutes: 0, value: '1:00 PM'},
    {hour: 13, minutes: 30, value: '1:30 PM'},
    {hour: 14, minutes: 0, value: '2:00 PM'},
    {hour: 14, minutes: 30, value: '2:30 PM'},
    {hour: 15, minutes: 0, value: '3:00 PM'},
    {hour: 15, minutes: 30, value: '3:30 PM'},
    {hour: 16, minutes: 0, value: '4:00 PM'},
    {hour: 16, minutes: 30, value: '4:30 PM'},
    {hour: 17, minutes: 0, value: '5:00 PM'},
    {hour: 17, minutes: 30, value: '5:30 PM'},
    {hour: 18, minutes: 0, value: '6:00 PM'},
    {hour: 18, minutes: 30, value: '6:30 PM'},
    {hour: 19, minutes: 0, value: '7:00 PM'},
    {hour: 19, minutes: 30, value: '7:30 PM'},
    {hour: 20, minutes: 0, value: '8:00 PM'},
    {hour: 20, minutes: 30, value: '8:30 PM'},
    {hour: 21, minutes: 0, value: '9:00 PM'},
    {hour: 21, minutes: 30, value: '9:30 PM'},
    {hour: 22, minutes: 0, value: '10:00 PM'},
    {hour: 22, minutes: 30, value: '10:30 PM'},
    {hour: 23, minutes: 0, value: '11:00 PM'},
    {hour: 23, minutes: 30, value: '11:30 PM'},
  ];

  private onChange: Function = (selectedTime: { hour: number; minutes: number }) => {
  };
  private onTouch: Function = () => {
  };

  writeValue(value: { hour: number; minutes: number }): void {
    let selectedTime;
    if (value) {
      const foundedSelectedTime = this.workingTime.find((time) => time.hour === value.hour && time.minutes === value.minutes);
      selectedTime = foundedSelectedTime ? foundedSelectedTime.value : null;
    } else {
      selectedTime = null;
    }
    this.selectedTimeFormControl = new FormControl(selectedTime);
  }

  registerOnChange(fn: Function): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: Function): void {
    this.onTouch = fn;
  }

  updateValue() {
    const updatedTime = this.workingTime.find((time) => this.selectedTimeFormControl.value === time.value);
    this.onChange({hour: updatedTime ? updatedTime.hour : null, minutes: updatedTime ? updatedTime.minutes : null});
  }
}
