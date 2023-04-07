import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { AddressModule } from '../../../../../components/address/address.module';
import { NameFormGroupModule } from '../../../../../components/name-form-group/name-form-group.module';
import { IfActiveUserDirectiveModule } from '../../../../../shared/directives/if-active-user/if-active-user-directive.module';

import { ProfileEditComponent } from './profile-edit.component';
import { UploadPhotoModule } from './upload-photo/upload-photo.module';
import { ContactInfoFormModule } from './contact-info-form/contact-info-form.module';
import { SocialMediaFormModule } from './social-media-form/social-media-form.module';
import { HoursFormModule } from './hours-form/hours-form.module';
import { LanguagesFormModule } from './languages-form/languages-form.module';
import { MembershipModule } from './membership/membership.module';
import { LicenseFormModule } from './license-form/license-form.module';
import { PracticeFormModule } from './practice-form/practice-form.module';
import { EducationFormModule } from './education-form/education-form.module';
import { ProfilePreviewModule } from '../profile-preview/profile-preview.module';
import { ChangePositionModule } from './change-position/change-position.module';
import { MyReviewModule } from '../my-review/my-review.module';
import { DeleteMyDataModule } from './delete-my-data/delete-my-data.module';
import { DeleteDataOfApplicantModule } from './delete-data-of-applicant/delete-data-of-applicant.module';
import { ChangeMembershipStatusModule } from './change-membership-status/change-membership-status.module';
import { LoginCredentialsModule } from './login-credentials/login-credentials.module';
import { PermanentlyDeleteEmployeeModule } from './permanently-delete-employee/permanently-delete-employee.module';

@NgModule({
  imports: [
    SharedModule,
    AddressModule,
    ContactInfoFormModule,
    ChangePositionModule,
    EducationFormModule,
    HoursFormModule,
    NameFormGroupModule,
    MembershipModule,
    LanguagesFormModule,
    LicenseFormModule,
    PracticeFormModule,
    SocialMediaFormModule,
    UploadPhotoModule,
    ProfilePreviewModule,
    MyReviewModule,
    DeleteMyDataModule,
    DeleteDataOfApplicantModule,
    ChangeMembershipStatusModule,
    IfActiveUserDirectiveModule,
    LoginCredentialsModule,
    PermanentlyDeleteEmployeeModule,
  ],
  declarations: [
    ProfileEditComponent,
  ],
  exports: [
    ProfileEditComponent,
  ],
})

export class ProfileEditModule {
}
