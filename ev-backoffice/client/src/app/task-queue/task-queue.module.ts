import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { TabsModule } from '../shared/components/tabs/tabs.module';
import { GUARD_PROVIDERS } from '../core/guards';

import { NgrxTaskQueueModule } from './ngrx/module';
import { TaskQueueComponent } from './task-queue.component';
import { TaskQueueRoutingModule } from './task-queue-routing.module';
import { RESOLVERS } from './resolvers';
import { CreateApplicantFormGroupService } from './package/services';
import { DispositionsService } from './dispositions/dispositions.service';

@NgModule({
  imports: [
    CommonModule,
    NgrxTaskQueueModule,
    TaskQueueRoutingModule,
    SharedModule,
    TabsModule,
  ],
  declarations: [
    TaskQueueComponent,
  ],
  providers: [
    RESOLVERS,
    GUARD_PROVIDERS,
    CreateApplicantFormGroupService,
    DispositionsService
  ]
})
export class TaskQueueModule { }
