import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-username-password',
  templateUrl: './username-password.component.html',
})
export class UsernamePasswordComponent {

  @Input() formGroup: FormGroup;
  @Input() submitted = false;

  get usernameControl() {
    return this.formGroup.get('username');
  }

  get passwordControl() {
    return this.formGroup.get('password');
  }

  get repeatPasswordControl() {
    return this.formGroup.get('repeatPassword');
  }
}
