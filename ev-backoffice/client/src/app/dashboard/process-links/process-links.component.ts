import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, map, pluck } from 'rxjs/operators';

import { UserService } from '../../core/services';
import { Role } from '../../core/models/role.enum';


export interface ProcessLink {
  title: string;
  titleUrl?: string;
  url: string;
}

@Component({
  selector: 'app-process-links',
  templateUrl: './process-links.component.html',
})

export class ProcessLinksComponent implements OnInit {
  links$: Observable<ProcessLink[]>;

  constructor(
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.links$ = this.userService.currentUser$.pipe(
      filter(user => !!user),
      pluck('roles'),
      map((roles: Role[]) => {
        const isClient = !!roles.find((role) => role === Role.ROLE_USER);
        const processLinksApplicant: ProcessLink[] = [
          {
            title: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.VISA_PROCESSING_TIME',
            titleUrl: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.LINK_GOES_HERE',
            url: 'https://egov.uscis.gov/processing-times/',
          },
          {
            title: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.VISA_INTERVIEW',
            titleUrl: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.LINK_GOES_HERE',
            url: 'https://travel.state.gov/content/travel/en/us-visas/visa-information-resources/wait-times.html',
          },
          {
            title: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.VISA_PRIORITY_DATES',
            titleUrl: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.LINK_GOES_HERE',
            url: 'https://travel.state.gov/content/travel/en/legal/visa-law0/visa-bulletin/2020/visa-bulletin-for-june-2020.html',
          },
          {
            title: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.US_EMBASSY',
            titleUrl: null,
            url: 'https://www.usembassy.gov/',
          }
        ];

        const processLinksAttorney: ProcessLink[] = [
          {
            title: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.VISA_PROCESSING_TIME',
            titleUrl: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.LINKS_GOES_HERE',
            url: 'https://egov.uscis.gov/processing-times/',
          },
          {
            title: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.VISA_INTERVIEW',
            titleUrl: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.LINKS_GOES_HERE',
            url: 'https://travel.state.gov/content/travel/en/us-visas/visa-information-resources/wait-times.html#:~:text=Important%20Not' +
              'ice%3A%20Except%20in%20c\n' +
              'ases,supplemental%20documents%2C%20whichever%20is%20later',
          },
          {
            title: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.VISA_PRIORITY_DATES',
            titleUrl: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.LINKS_GOES_HERE',
            url: 'https://travel.state.gov/content/travel/en/legal/visa-law0/visa-bulletin/2020/visa-bulletin-for-june-2020.html',
          },
          {
            title: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.US_EMBASSY',
            titleUrl: 'TEMPLATE.DASHBOARD.PROCESS_LINKS.LINKS_GOES_HERE',
            url: 'https://www.usembassy.gov/',
          }
        ];
        return isClient ? processLinksApplicant : processLinksAttorney;
      })
    );
  }
}
