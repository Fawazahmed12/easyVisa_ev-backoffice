import { Component } from '@angular/core';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.component.html',
})
export class NotFoundComponent {

  constructor(
    private activatedRoute: ActivatedRoute,
  ) {
  }

  get route$() {
    return this.activatedRoute.url.pipe(
      map((urlSegments: UrlSegment[]) =>
        urlSegments
        .map((segment: UrlSegment) => segment.path)
        .join('/')
      )
    );
  }
}
