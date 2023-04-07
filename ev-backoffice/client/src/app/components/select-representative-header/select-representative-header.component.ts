import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { distinctUntilChanged, filter, tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { Organization } from '../../core/models/organization.model';
import { OrganizationService, PackagesService, UserService } from '../../core/services';
import { User } from '../../core/models/user.model';
import { AttorneyMenu } from '../../core/models/attorney.model';
import { Router } from '@angular/router';

@Component({
  selector: ' app-select-representative-header',
  templateUrl: './select-representative-header.component.html',
  styles: [
    `.custom-padding{
      padding: 0.2rem 0;
    }
    `
  ]
})
@DestroySubscribers()
export class SelectRepresentativeHeaderComponent implements OnDestroy, AddSubscribers, OnInit {
  @Input()  representativeIdFormControl: FormControl;
  @Input() count: number = null;
  @Input() countLabel: string = null;

  changeColor$: Observable<boolean>;
  representativeMenu$: Observable<AttorneyMenu[]>;

  private subscribers: any = {};

  activeOrganization$: Observable<Organization> = this.organizationService.activeOrganization$;
  currentUser$: Observable<User> = this.userService.currentUser$;


  constructor(
    private organizationService: OrganizationService,
    private userService: UserService,
    private packagesService: PackagesService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.representativeMenu$ = this.organizationService.representativesMenu$;
  }

  addSubscribers() {
    this.subscribers.representativeIdSubscription = this.representativeIdFormControl.valueChanges.pipe(
      filter((value) => value !== undefined),
    )
    .subscribe((id) => {
      this.organizationService.UpdateCurrentRepresentative(id);
      this.reloadCurrentRoute();
    });
  }

  reloadCurrentRoute() {
    this.packagesService.clearActivePackage();
    const currentUrl = this.router.url;
    const hasIncludesQuestionnaireOrDocuments = currentUrl.includes('questionnaire') || currentUrl.includes('documents');
    if (hasIncludesQuestionnaireOrDocuments) {
      this.router.navigate(['/']);
      return;
    }
    this.router.navigateByUrl(currentUrl);
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
