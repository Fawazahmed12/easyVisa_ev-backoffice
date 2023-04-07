import { Component } from '@angular/core';
import { BaseFieldWrapperComponent } from '../../base-field-wrapper';

@Component({
  selector: 'app-asset-warning-info',
  template: `
    <div class="row">
      <div *ngIf="to.label" [innerHtml]="to.label"></div>
    </div>
  `
})
export class AssetWarningWrapperComponent extends BaseFieldWrapperComponent {
  constructor() {
    super();
  }
}
