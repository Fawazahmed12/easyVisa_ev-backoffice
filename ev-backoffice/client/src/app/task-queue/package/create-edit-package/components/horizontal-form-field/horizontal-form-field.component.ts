import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-horizontal-form-field',
  templateUrl: './horizontal-form-field.component.html',
})
export class HorizontalFormFieldComponent {

  @Input() label: string;

  @Input() control: FormControl;

  @Input() type = 'text';

  @Input() required = false;

  @Input() submitted = false;

  @Input() col3Label = false;

  @Input() col4Label = false;

  get borderDanger() {
    return this.submitted && this.control.invalid ? 'border-danger' : '';
  }
}
