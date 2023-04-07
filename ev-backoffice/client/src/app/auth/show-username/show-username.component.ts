import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-show-username',
  templateUrl: './show-username.component.html',
  styleUrls: ['./show-username.component.scss'],
})
export class ShowUsernameComponent implements OnInit {
  public showUsername$: Observable<{username: string; message: string}>;
  constructor(
    private activatedRoute: ActivatedRoute,
  ) {

  }
  ngOnInit() {
    this.showUsername$ = this.activatedRoute.data.pipe(
      map((data: any) => data.showUsername)
    );
  }
}
