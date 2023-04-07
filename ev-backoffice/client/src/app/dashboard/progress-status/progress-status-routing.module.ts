import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RoleGuardService } from '../../core/guards/role-guard.service';
import { Role } from '../../core/models/role.enum';

import { ProgressStatusComponent } from './progress-status.component';
import { QuestionnaireStatusResolverService } from './resolvers/questionnaire-status-resolver.service';
import { DocumentStatusResolverService } from './resolvers/document-progress-resolver.service';
import { MyPackagesResolverService } from '../../core/resolvers/my-packages-resolver.service';

const routes: Routes = [
  {
    path: '',
    component: ProgressStatusComponent,
    resolve: [
      MyPackagesResolverService,
      QuestionnaireStatusResolverService,
      DocumentStatusResolverService,
    ],
    canActivate: [RoleGuardService],
    data: { roles: [Role.ROLE_USER] }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProgressStatusRoutingModule {
}
