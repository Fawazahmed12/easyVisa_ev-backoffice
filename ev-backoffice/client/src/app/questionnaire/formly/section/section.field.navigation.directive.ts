import { Directive, ElementRef, HostListener } from '@angular/core';


@Directive({
  selector: '[appSectionFieldNavigation]'
})
export class SectionFieldNavigationDirective {

  constructor(private el: ElementRef) {
  }

  @HostListener('keydown', [ '$event' ]) onKeydownHandler(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      const form = this.el.nativeElement;
      const focusableList = Array.from(form.querySelectorAll('input,a,select,button,textarea')).filter((data: any) => !data.hidden);
      const tmp = focusableList.indexOf(event.target);
      const nextIndex = focusableList.indexOf(event.target) === focusableList.length - 1 ? 0 : focusableList.indexOf(event.target) + 1;
      const next: any = focusableList[ nextIndex ];
      next.focus();
    }
  }
}
