import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { EditPreviewProfileComponent } from './edit-preview-profile.component';
import { EditPreviewProfileRoutingModule } from './edit-preview-profile-routing.module';
import { EditPreviewProfileService } from './edit-preview-profile.service';
import { ProfileEditModule } from './components/profile-edit/profile-edit.module';
import { RESOLVERS } from './resolvers';
import { ProfilePreviewModule } from './components/profile-preview/profile-preview.module';
import { WarningPersonalPageModule } from '../../../components/warning-personal-page/warning-personal-page.module';

@NgModule({
  imports: [
    SharedModule,
    EditPreviewProfileRoutingModule,
    ProfileEditModule,
    ProfilePreviewModule,
    WarningPersonalPageModule,
  ],
  declarations: [
    EditPreviewProfileComponent,
  ],
  providers: [
    EditPreviewProfileService,
    RESOLVERS
  ]
})

export class EditPreviewProfileModule {
}
