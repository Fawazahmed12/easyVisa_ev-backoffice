import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[appInsertComponent]',
})
export class InsertComponentDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
