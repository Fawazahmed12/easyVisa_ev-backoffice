import {
  Component,
  OnDestroy,
  ViewChild,
  ComponentFactoryResolver,
  OnInit
} from '@angular/core';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { debounceTime } from 'rxjs/operators';

import { NotificationsService } from '../../core/services';
import { InsertComponentDirective } from '../../shared/directives/insert-component/insert-component.directive';

@Component({
  selector: 'app-logo-message',
  templateUrl: './logo-message.component.html',
})

@DestroySubscribers()
export class LogoMessageComponent implements OnDestroy, OnInit, AddSubscribers {
  @ViewChild(InsertComponentDirective, { static: true }) adHost: InsertComponentDirective;

  private subscribers: any = {};

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver,
    private notificationsService: NotificationsService,
  ) {
  }

    ngOnInit() {
      console.log(`${this.constructor.name} Init`);
    }

    addSubscribers() {
      this.subscribers.showMessageSubscription = this.notificationsService.showComponent$.pipe(
        debounceTime(1000),
      ).subscribe(component => !!component ? this.loadComponent(component) : this.clearComponent());
    }

    ngOnDestroy() {
      console.log(`${this.constructor.name} Destroys`);
    }

    loadComponent(component) {
      const componentFactory = this.componentFactoryResolver.resolveComponentFactory(component);
      const viewContainerRef = this.adHost.viewContainerRef;
      viewContainerRef.clear();
      viewContainerRef.createComponent(componentFactory);
    }

    clearComponent() {
      this.adHost.viewContainerRef.clear();
    }
}
