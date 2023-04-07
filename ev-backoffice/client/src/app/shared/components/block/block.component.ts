import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-block',
  templateUrl: './block.component.html',
  styleUrls: ['./block.component.scss']
})

export class BlockComponent {
  @Input() title = '';
  @Input() smallTitle = false;
  @Input() customPadding = false;
}
