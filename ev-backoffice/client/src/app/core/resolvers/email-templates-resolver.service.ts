import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { filter, switchMap, take } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { EmailTemplatesService, OrganizationService } from '../services';


@Injectable()
export class EmailTemplatesResolverService implements Resolve<any> {

  private currentRepresentativeId$: Observable<number> = this.organizationService.currentRepresentativeId$;

  constructor(
    private emailTemplatesService: EmailTemplatesService,
    private organizationService: OrganizationService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const emailTemplateTypes = route.data.emailTemplateTypes;
    return this.organizationService.representativesMenuRequestState$.pipe(
      filter((request) => !request.loading && !!request.data),
      switchMap((res: any) => this.currentRepresentativeId$.pipe(
          filter((currentRepresentativeId) => !!currentRepresentativeId),
          switchMap((currentRepresentativeId: number) => {
            const params = {
              templateType: emailTemplateTypes,
              representativeId: currentRepresentativeId.toString(),

            };
            return this.emailTemplatesService.getEmailTemplates(params);
          })
        )),
      take(1),
    );
  }
}
