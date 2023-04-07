import { Injectable } from '@angular/core';
import {
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot, CanDeactivate,
} from '@angular/router';
import { FormGroup } from '@angular/forms';

import { Observable } from 'rxjs';

import { SubmitArticleComponent } from '../../dashboard/articles/submit-article/submit-article.component';
import { ArticlesService } from '../../dashboard/articles/articles.service';

import { ModalService } from '../services';


@Injectable()
export class SubmitArticleGuardService implements CanDeactivate<SubmitArticleComponent> {
  formGroup: FormGroup;

  constructor(
    private router: Router,
    private modalService: ModalService,
    private articlesService: ArticlesService,
  ) {
  }


  canDeactivate(
    component: SubmitArticleComponent,
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.articlesService.isArticleChanges();
  }
}

