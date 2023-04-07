import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { combineLatest, Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

import { PackagesService, UserService } from '../../core/services';
import { Package } from '../../core/models/package/package.model';
import { RequestState } from '../../core/ngrx/utils';

import { ProgressStatusService } from './progress-status.service';
import { ProgressStatus } from './models/progress-status.model';

@Component({
  selector: 'app-progress-status',
  templateUrl: './progress-status.component.html',
})
@DestroySubscribers()
export class ProgressStatusComponent implements OnInit, AddSubscribers, OnDestroy {
  packageFormControl: FormControl;

  packages$: Observable<Package[]>;

  activePackageId$: Observable<number>;
  questionnaireProgressGetState$: Observable<RequestState<ProgressStatus[]>>;
  documentProgressGetState$: Observable<RequestState<ProgressStatus[]>>;
  isLoading$: Observable<boolean>;


  private subscribers: any = {};

  constructor(
    private userService: UserService,
    private packagesService: PackagesService,
    private progressStatusService: ProgressStatusService,
  ) {
  }

  ngOnInit() {
    this.packages$ = this.packagesService.packages$;
    this.activePackageId$ = this.packagesService.activePackageId$;
    this.questionnaireProgressGetState$ = this.progressStatusService.questionnaireProgressGetState$;
    this.documentProgressGetState$ = this.progressStatusService.documentProgressGetState$;

    this.isLoading$ = combineLatest([
      this.progressStatusService.questionnaireProgressGetState$.pipe(
        map((request) => request.loading && !request.loaded)
      ),
      this.progressStatusService.documentProgressGetState$.pipe(
        map((request) => request.loading && !request.loaded)
      )
    ]).pipe(
      map(([questionnaireProgressLoading, documentProgressLoading]) => questionnaireProgressLoading || documentProgressLoading)
    );
  }

  addSubscribers() {
    this.subscribers.activePackageSubscription = this.activePackageId$.pipe(
      take(1),
    ).subscribe(
      id => this.packageFormControl = new FormControl(id || null)
    );

    this.subscribers.packageFormControlSubscription = this.packageFormControl.valueChanges.subscribe(
      id => this.packagesService.setActivePackage(id)
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroy`);
  }
}
