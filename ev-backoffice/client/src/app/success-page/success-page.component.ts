import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-success-page',
  templateUrl: './success-page.component.html',
  styleUrls: ['./success-page.component.scss']
})

export class SuccessPageComponent implements OnInit {

  public header$: Observable<string>;
  public text$: Observable<string>;

  constructor(
    private activatedRoute: ActivatedRoute,
  ) {
  }

  ngOnInit() {
    this.header$ = this.activatedRoute.data.pipe(
      map((data) => `${data.translationPath}.HEADER`)
    );
    this.text$ = this.activatedRoute.data.pipe(
      map((data) => `${data.translationPath}.TEXT`)
    );
  }

}
