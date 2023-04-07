import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-name-form-group',
  templateUrl: './name-form-group.component.html',
})

export class NameFormGroupComponent {
  @Input() firstFormControl: FormControl;
  @Input() middleFormControl: FormControl;
  @Input() lastFormControl: FormControl;
  @Input() col3Label = false;
  @Input() col4Label = false;
  @Input() submitted = false;
  @Input() firstFormControlLabel = 'FORM.LABELS.FIRST';
  @Input() middleFormControlLabel = 'FORM.LABELS.MIDDLE';
  @Input() lastFormControlLabel = 'FORM.LABELS.LAST';

}
