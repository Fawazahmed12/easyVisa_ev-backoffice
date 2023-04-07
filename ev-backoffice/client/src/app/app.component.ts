import { Component, OnInit } from '@angular/core';
import { NavigationError, Router, ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';

import { filter } from 'rxjs/operators';

import { AuthService } from './core/services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: [ './app.component.scss' ]
})
export class AppComponent implements OnInit {

  isLoggedIn$: Observable<boolean>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    public authService: AuthService
  ) {}

  ngOnInit() {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
    this.router.events.pipe(
      filter(e => e instanceof NavigationError)
    ).subscribe(
      (e: NavigationError)  => {
          this.router.navigate(['/', '404'], {relativeTo: this.activatedRoute, fragment: e.url});
      });
  }
}
