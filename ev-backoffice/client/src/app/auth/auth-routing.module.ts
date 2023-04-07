import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthComponent } from './auth.component';
import { I18nResolverService } from '../core/i18n/i18n-resolver.service';
import { RegistrationFinishGuardService } from '../core/guards';
import { LoggedInGuardService } from './guards/';
import { SignUpSuccessGuardService } from './guards';
import { ConvertToAttorneyGuardService } from '../core/guards/convert-to-attorney-guard.service';

export const routes: Routes = [
  {
    path: '',
    component: AuthComponent,
    children: [
      {path: '', redirectTo: 'login', pathMatch: 'full'},
      {
        path: 'login',
        loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'registration',
        loadChildren: () => import('./registration/registration.module').then(m => m.RegistrationModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'retrieve-credential',
        loadChildren: () => import('./retrieve-credential/retrieve-credential.module').then(m => m.RetrieveCredentialModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'reset-password',
        loadChildren: () => import('./reset-password/reset-password.module').then(m => m.ResetPasswordModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'attorney-sign-up',
        loadChildren: () => import('./attorney-sign-up/attorney-sign-up.module').then(m => m.AttorneySignUpModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'sign-up',
        loadChildren: () => import('./sign-up/sign-up.module').then(m => m.SignUpModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'attorney-landing-page',
        loadChildren: () => import('./attorney-landing-page/attorney-landing-page.module').then(m => m.AttorneyLandingPageModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'rep-basic-info',
        loadChildren: () => import('./rep-basic-info/rep-basic-info.module').then(m => m.RepBasicInfoModule),
        data: {step: 2},
        canActivate: [RegistrationFinishGuardService],
      },
      {
        path: 'pay-sign-up-fee',
        loadChildren: () => import('./pay-sign-up-fee/pay-sign-up-fee.module').then(m => m.PaySignUpFeeModule),
        data: {step: 3},
        canActivate: [RegistrationFinishGuardService],
      },
      {
        path: 'retrieve-credential-success',
        loadChildren: () => import('../success-page/success-page.module').then(m => m.SuccessPageModule),
        data: {translationPath: 'TEMPLATE.AUTH.SUCCESS.RETRIEVE_CREDENTIAL_SUCCESS'},
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'sign-up-success',
        loadChildren: () => import('./sign-up-success/sign-up-success.module').then(m => m.SignUpSuccessModule),
        canActivate: [SignUpSuccessGuardService],
      },
      {
        path: 'verify-registration',
        loadChildren: () => import('./verify-registration/verify-registration.module').then(m => m.VerifyRegistrationModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'attorney-welcome',
        loadChildren: () => import('./attorney-welcome/attorney-welcome.module').then(m => m.AttorneyWelcomeModule),
        canActivate: [RegistrationFinishGuardService],
      },
      {
        path: 'client-welcome',
        loadChildren: () => import('./client-welcome/client-welcome.module').then(m => m.ClientWelcomeModule),
      },
      {
        path: 'standard-ev-charges',
        loadChildren: () => import('./standard-ev-charges/standard-ev-charges.module').then(m => m.StandardEvChargesModule),
        data: {step: 2},
        canActivate: [RegistrationFinishGuardService],
      },
      {
        path: 'show-username',
        loadChildren: () => import('./show-username/show-username.module').then(m => m.ShowUsernameModule),
        canActivate: [LoggedInGuardService],
      },
      {
        path: 'representative-message-page',
        loadChildren: () =>
          import('./representative-message-page/representative-message-page.module')
          .then(m => m.RepresentativeMessagePageModule),
        data: {step: 2},
        canActivate: [RegistrationFinishGuardService],
      },
      {
        path: 'rep-info-payment-method',
        loadChildren: () =>
          import('./representative-info-payment-method-page/representative-info-payment-method-page.module')
          .then(m => m.RepresentativeInfoPaymentMethodPageModule),
        canActivate: [ConvertToAttorneyGuardService]
      },
    ],
    resolve: {
      translation: I18nResolverService,
    },
    data: {
      translationUrl: 'auth-module',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuthRoutingModule {
}
