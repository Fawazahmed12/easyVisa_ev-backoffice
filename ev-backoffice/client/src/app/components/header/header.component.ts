import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styles: [
    `.app-header{
      height: 72px;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }
    `
  ]
})
export class HeaderComponent {
}
