import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, withLatestFrom } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

@Component({
  selector: 'app-not-found-error',
  templateUrl: './not-found-error.component.html',
})
@DestroySubscribers()
export class NotFoundErrorComponent implements OnInit, AddSubscribers, OnDestroy {
  failedUrl$: Observable<string>;
  reloadPage$: Subject<any> = new Subject();

  private subscribers: any = {};


  constructor(
    private activatedRoute: ActivatedRoute,
  ) {
  }

  ngOnInit() {
    this.failedUrl$ = this.activatedRoute.fragment.pipe(
      map(fragment => !!fragment ? fragment : '/account/profile')
    );
  }

  addSubscribers() {
    this.subscribers.redirectSubscription = this.reloadPage$.pipe(
      withLatestFrom(this.failedUrl$),
    ).subscribe(([, url]) => window.location.href = url);
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  redirect() {
    this.reloadPage$.next(true);
  }
}
