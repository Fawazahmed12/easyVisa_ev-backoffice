import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';

import { RepresentativesCount } from '../../models/representatives-count.model';

import { DashboardSettingsService } from '../settings.service';


@Component({
  selector: 'app-representatives-count',
  templateUrl: './representatives-count.component.html',
})

export class RepresentativesCountComponent implements OnInit {
  representativesCount$: Observable<RepresentativesCount>;

  constructor(
    private dashboardSettingsService: DashboardSettingsService,
  ) {
  }

  ngOnInit() {
    this.representativesCount$ = this.dashboardSettingsService.representativesCount$;
  }
}

