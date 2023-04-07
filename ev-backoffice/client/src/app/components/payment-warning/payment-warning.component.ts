import { Component, OnInit } from '@angular/core';
import { UserService } from '../../core/services';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Attorney } from '../../core/models/attorney.model';


@Component({
  selector: 'app-payment-warning',
  templateUrl: './payment-warning.component.html',
})
export class PaymentWarningComponent implements OnInit {
  balance$: Observable<number>;

  constructor(
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.balance$ = this.userService.currentUser$.pipe(
      filter((user) => !!user),
      map((user) => (user.profile as Attorney).balance)
    );
  }
}
