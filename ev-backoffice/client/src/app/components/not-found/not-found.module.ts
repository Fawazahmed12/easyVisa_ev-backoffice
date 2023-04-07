import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { NotFoundComponent } from './not-found.component';

@NgModule({
  declarations: [
    NotFoundComponent,
  ],
  exports: [
    NotFoundComponent,
  ],
  imports: [
    SharedModule,
  ],
})
export class NotFoundModule {
}
