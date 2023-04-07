import {HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {NgbCalendar, NgbDate} from '@ng-bootstrap/ng-bootstrap';

@Injectable()
export class CurrentDateInterceptor implements HttpInterceptor {

  constructor(private calendar: NgbCalendar) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const today: NgbDate = this.calendar.getToday();
    const dateValue = today.year + '-' + today.month + '-' + today.day;
    const httpRequest = req.clone({
      headers: req.headers.append('Current-Date', dateValue)
    });
    return next.handle(httpRequest);
  }
}
