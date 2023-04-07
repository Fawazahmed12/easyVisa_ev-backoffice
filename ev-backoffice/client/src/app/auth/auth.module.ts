import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthRoutingModule } from './auth-routing.module';
import { AuthComponent } from './auth.component';

import { NgrxAuthModule } from './ngrx/module';

import { RESOLVERS } from './resolvers';
import { PROVIDERS } from './services';
import { GUARD_PROVIDERS } from './guards';

@NgModule({
  imports: [
    CommonModule,
    AuthRoutingModule,
    NgrxAuthModule,
  ],
  declarations: [
    AuthComponent,
  ],
  providers: [
    GUARD_PROVIDERS,
    PROVIDERS,
    RESOLVERS,
  ],
})
export class AuthModule { }
