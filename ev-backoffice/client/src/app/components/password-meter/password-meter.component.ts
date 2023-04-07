import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';

import { Observable, ReplaySubject } from 'rxjs';
import { debounceTime, map, share, withLatestFrom } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { ZXCVBNResult } from 'zxcvbn';
import { measurePasswordStrength } from '../../auth/utils/measure-password-strength';


@Component({
  selector: 'app-password-meter',
  templateUrl: './password-meter.component.html',
  styleUrls: ['./password-meter.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
@DestroySubscribers()
export class PasswordMeterComponent implements OnInit, OnDestroy, AddSubscribers {

  @Input() col3Label = false;
  @Input() col4Label = false;
  @Input() noOffset = false;

  strength$: Observable<ZXCVBNResult>;

  password$: ReplaySubject<string> = new ReplaySubject<string>(1);

  isRecommended$: Observable<any>;

  suggestions$: Observable<string[]>;

  @Input('password')
  set password(value: string) {
    this.password$.next(value);
  }

  @Output() strengthChange = new EventEmitter<ZXCVBNResult>();

  private subscribers: any = {};

  ngOnInit() {
    this.strength$ = this.password$.pipe(
      map((password) => this.measureStrength(password)),
      share(),
    );

    this.suggestions$ = this.strength$.pipe(
      debounceTime(500),
      map((result) => {
        if (result && result.feedback) {
          const suggestions = result.feedback.suggestions || [];
          const warning = result.feedback.warning || '';
          return suggestions.concat(warning).filter((item) => !!item);
        }
        return ['FORM.METER.PASSWORD_RECOMMENDATION'];
      }),
    );

    this.isRecommended$ = this.suggestions$.pipe(
      withLatestFrom(this.password$),
      map(([suggestions, password]) =>
        (!password.length || suggestions.length)
      ),
    );
  }

  addSubscribers() {
    this.subscribers.strengthSubscription = this.strength$.subscribe(this.strengthChange);
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  private measureStrength(password: string) {
    if (password.length) {
      return measurePasswordStrength(password);
    }
  }

}
