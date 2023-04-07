import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { I18nResolverService } from '../core/i18n/i18n-resolver.service';
import { MyPaymentMethodResolverService } from '../core/resolvers/my-payment-method-resolver.service';
import { MyBalanceResolverService } from '../core/resolvers/my-balance-resolver.service';
import { MyAccountTransactionsResolverService } from '../core/resolvers/my-account-transactions-resolver.service';
import { ActiveMembershipGuardService } from '../core/guards/active-membership-guard.service';

import { UnpaidGuardService } from '../core/guards/unpaid-guard.service';
import { PositionGuardService } from '../core/guards/position-guard.service';
import { RoleGuardService } from '../core/guards/role-guard.service';
import { Role } from '../core/models/role.enum';

import { AccountComponent } from './account.component';
import { EmployeePosition } from './permissions/models/employee-position.enum';


export const routes: Routes = [
  {
    path: '',
    component: AccountComponent,
    children: [
      { path: '', redirectTo: 'profile', pathMatch: 'full' },
      { path: 'profile',
        loadChildren: () => import('./profile/profile.module').then(m => m.ProfileModule),
        canActivate: [UnpaidGuardService]
      },
      {
        path: 'payment-fee-schedule',
        loadChildren: () => import('./payment-fee-schedule/payment-fee-schedule.module').then(m => m.PaymentFeeScheduleModule),
        resolve: [
          MyPaymentMethodResolverService,
          MyBalanceResolverService,
          MyAccountTransactionsResolverService
        ],
        canActivate: [PositionGuardService],
        data: {
          positions: [
            EmployeePosition.PARTNER,
            EmployeePosition.ATTORNEY,
            EmployeePosition.MANAGER,
            EmployeePosition.EMPLOYEE,
          ]
        }
      },
      {
        path: 'email-templates',
        loadChildren: () => import('./email-templates/email-templates.module').then(m => m.EmailTemplatesModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService,
          PositionGuardService,
        ],
        data: {
          positions: [
            EmployeePosition.PARTNER,
            EmployeePosition.ATTORNEY,
            EmployeePosition.MANAGER,
            EmployeePosition.EMPLOYEE,
          ]
        }
      },
      {
        path: 'permissions',
        loadChildren: () => import('./permissions/permissions.module').then(m => m.PermissionsModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService,
          RoleGuardService
        ],
        data: {
          roles: [
            Role.ROLE_OWNER,
            Role.ROLE_EV,
            Role.ROLE_ATTORNEY,
            Role.ROLE_EMPLOYEE
          ]
        }
      },
      {
        path: 'write-a-review',
        loadChildren: () => import('./write-review/write-review.module').then(m => m.WriteReviewModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService,
          RoleGuardService
        ],
        data: {
          roles: [ Role.ROLE_USER ]
        }
      },
      {
        path: 'legal-representative',
        loadChildren: () => import('./legal-representative/legal-representative.module').then(m => m.LegalRepresentativeModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService,
          RoleGuardService,
        ],
        data: {
          roles: [ Role.ROLE_USER ]
        }
      },
      {
        path: 'notifications-reminders',
        loadChildren: () => import('./notifications-reminders/notifications-reminders.module').then(m => m.NotificationsRemindersModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService,
        ],
      },
    ],
    resolve: {
      translation: I18nResolverService,
    },
    data: {
      translationUrl: 'account-module',
    }
  },
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ],
})
export class AccountRoutingModule {
}
