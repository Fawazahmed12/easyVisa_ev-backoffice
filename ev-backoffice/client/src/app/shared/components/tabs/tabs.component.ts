import { Component, Input } from '@angular/core';
import { Tab } from '../../../core/models/tab.model';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss'],
})

export class TabsComponent {
  @Input() tabs: Tab[];

  getAbsoluteCount(count: number){
    return Math.max(0,count)
  }
}
