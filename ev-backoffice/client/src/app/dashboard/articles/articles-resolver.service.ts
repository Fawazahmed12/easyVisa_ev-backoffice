import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { switchMap, take } from 'rxjs/operators';

import { OrganizationService } from '../../core/services/organization.service';

import { ArticlesService } from './articles.service';


@Injectable()
export class ArticlesResolverService implements Resolve<any> {

  constructor(
    private organizationService: OrganizationService,
    private articlesService: ArticlesService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.currentRepIdOrgId$.pipe(
      switchMap(([representativeId, activeOrganizationId]) => {
        const params = {
          sort: route.queryParams.sort || 'title',
          order: route.queryParams.order || 'asc',
          organizationId: route.queryParams.organizationId || activeOrganizationId,
          representativeId: route.queryParams.representativeId || representativeId,
          max: '25',
          offset: 0
        };
        return this.articlesService.getArticles(params);
      }),
      take(1)
    );
  }
}
