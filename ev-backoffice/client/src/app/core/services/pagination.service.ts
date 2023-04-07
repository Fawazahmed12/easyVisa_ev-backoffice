import { Injectable } from '@angular/core';
import { NavigationStart, Router } from "@angular/router";
import url from "url";

@Injectable({
  providedIn: 'root'
})
export class PaginationService {

  private pathListWithPagination: string[] = [
    '/task-queue/clients',
    '/task-queue/alerts',
    '/task-queue/warnings',
    '/task-queue/dispositions',
    '/dashboard/articles',
    '/account/payment-fee-schedule'
  ];

  constructor(private router: Router) {}

  updateOffset(e, formControl) {
    if (e instanceof NavigationStart && e.navigationTrigger === 'popstate') {
      const { pathname, href } = url.parse(e.url)
      if (this.pathListWithPagination.includes(pathname)) {
        const searchParams = new URL(`${window.location.origin}${href}`).searchParams
        const offset = searchParams.get('offset') || 0;
        if (offset !== (formControl.value)) {
          formControl.patchValue(offset);
        }
      }
    }
  }

  getHistoryNavigationSubscription(formControl) {
    return this.router.events.subscribe(e => {
      this.updateOffset(e, formControl);
    })
  }
}
