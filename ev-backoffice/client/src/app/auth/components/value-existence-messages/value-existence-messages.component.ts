import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-value-existence-messages',
  templateUrl: './value-existence-messages.component.html',
})
export class ValueExistenceMessagesComponent {
  @Input() control: FormControl;
  @Input() controlLabel: string;
}
