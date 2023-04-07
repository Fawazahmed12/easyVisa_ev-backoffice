import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-name-email',
  templateUrl: 'name-email.component.html',
})
export class NameEmailComponent {

  @Input() formGroup: FormGroup;
  @Input() submitted = false;
  @Input() isAttorney = false;

  get firstNameControl() {
    return this.formGroup.get('firstName');
  }

  get middleNameControl() {
    return this.formGroup.get('middleName');
  }

  get lastNameControl() {
    return this.formGroup.get('lastName');
  }

  get emailControl() {
    return this.formGroup.get('email');
  }
}
