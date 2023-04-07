import { Component, ViewChild, ViewContainerRef, ViewEncapsulation } from '@angular/core';
import { FieldWrapper, FormlyFieldConfig } from '@ngx-formly/core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-help-popup-wrapper',
  template: `
    <div class="row">
      <div class="col-3">
        <button
          type="button"
          class="btn btn-outline-info"
          placement="bottom"
        >{{ 'TEMPLATE.HELP' | translate }}
        </button>
      </div>
      <div class="col-9">
        <ng-template #fieldComponent></ng-template>
      </div>
    </div>
    <ng-template #popTemplate>
      <div *ngIf="field.tooltipHtml" [innerHtml]="tooltipHtml"></div>
      <div *ngIf="!field.tooltipHtml && field.tooltip">{{ field.tooltip | translate }}</div>
    </ng-template>
  `,
  styles: [
      `
      .tooltip-inner {
        min-width: 150px;
      }
    `
  ]
})
export class HelpPopupWrapperComponent extends FieldWrapper {
  field: FormlyFieldConfig & { tooltip: string; tooltipHtml: string };
  @ViewChild('fieldComponent', { read: ViewContainerRef, static: true }) fieldComponent: ViewContainerRef;

  constructor(
    private domSanitizer: DomSanitizer,
  ) {
    super();
  }

  get tooltipHtml() {
    return this.domSanitizer.bypassSecurityTrustHtml(this.field.tooltipHtml);
  }
}
