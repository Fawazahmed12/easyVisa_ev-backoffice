import { Injectable } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable()
export class NotificationsServiceMock {
  taskQueueNotificationsSubject$ = new ReplaySubject<boolean>(1);

  get taskQueueNotifications$(): Observable<boolean> {
    return this.taskQueueNotificationsSubject$.asObservable();
  }
}
