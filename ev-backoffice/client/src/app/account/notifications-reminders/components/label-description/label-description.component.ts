import { Component, EventEmitter, Input, Output } from '@angular/core';

import { NotificationCategory } from '../../../models/notyfication-types.model';


@Component({
  selector: 'app-label-description',
  templateUrl: './label-description.component.html',
})

export class LabelDescriptionComponent {
  @Input() items: NotificationCategory[];
  @Input() activeItem: string;
  @Input() title: string;
  @Output() setActiveItem = new EventEmitter();

  setItem(value) {
    this.setActiveItem.emit(value);
  }
}
