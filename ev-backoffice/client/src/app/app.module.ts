import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { SharedModule } from './shared/shared.module';
import { CoreModule } from './core/core.module';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';

import { NavModule } from './components/nav/nav.module';
import { HeaderModule } from './components/header/header.module';
import { NotFoundModule } from './components/not-found/not-found.module';
import { NotFoundErrorModule } from './components/not-found-error/not-found-error.module';
import { SpinnerModule } from './components/spinner/spinner.module';
import { FooterModule } from './components/footer/footer.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NavModule,
    HeaderModule,
    SharedModule,
    CoreModule,
    NotFoundModule,
    NotFoundErrorModule,
    SpinnerModule,
    FooterModule
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
}
