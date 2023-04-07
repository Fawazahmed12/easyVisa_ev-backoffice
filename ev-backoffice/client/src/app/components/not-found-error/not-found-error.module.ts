import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { NotFoundErrorComponent } from './not-found-error.component';

@NgModule({
  declarations: [
    NotFoundErrorComponent,
  ],
  exports: [
    NotFoundErrorComponent,
  ],
  imports: [
    SharedModule,
  ],
})
export class NotFoundErrorModule {
}
