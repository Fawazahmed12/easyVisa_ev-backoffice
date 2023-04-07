import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

import { lastIndexOf } from 'lodash-es';

@Component({
  selector: 'app-search-filter-element',
  templateUrl: './search-filter-element.component.html',
})

export class SearchFilterElementComponent {
  @Input() control: {control: FormControl; label: string; isDate: boolean};
  @Input() showComma = false;
  @Input() last: boolean;
}
