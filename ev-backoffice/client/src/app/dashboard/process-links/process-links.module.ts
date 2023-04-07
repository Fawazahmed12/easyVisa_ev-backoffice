import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { ProcessLinksComponent } from './process-links.component';
import { ProcessLinksRoutingModule } from './process-links-routing.module';


@NgModule({
  imports: [
    SharedModule,
    ProcessLinksRoutingModule,
  ],
  declarations: [ProcessLinksComponent],
})
export class ProcessLinksModule {
}
