import { Injectable } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';

import { DATE_PATTERN } from '../../../../../shared/validators/constants/date-pattern.const';

import { Education } from '../../models/education.model';
import { LicensedRegions } from '../../models/licensed-regions.model';
import { workingDays } from '../../models/working-days';

@Injectable()
export class ProfileEditService {
  profileFormGroup: FormGroup;
  workingDays = workingDays;

  get licensedRegionsFormArray() {
    return this.profileFormGroup.get('licensedRegions') as FormArray;
  }

  get educationFormArray() {
    return this.profileFormGroup.get('education') as FormArray;
  }

  get workingHoursFormArray() {
    return this.profileFormGroup.get('workingHours') as FormArray;
  }

  createAttorneyProfileFormGroup(data?) {
    return this.profileFormGroup = new FormGroup({
      awards: new FormControl(data.awards || null),
      firstName: new FormControl(data.firstName || null, Validators.required),
      middleName: new FormControl(data.middleName || null),
      lastName: new FormControl(data.lastName || null, Validators.required),
      officeAddress: this.createAddressFormGroup(data && data.officeAddress || null),
      stateBarNumber: new FormControl(data.stateBarNumber || null),
      summary: new FormControl(data.summary || null),
      uscisOnlineAccountNo: new FormControl(data.uscisOnlineAccountNo || null),
      workingHours: this.createHoursFormArray(data.workingHours || []),
      languages: new FormControl(data.languages || []),
      licensedRegions: this.createLicensedFormArray(data.licensedRegions || []),
      officePhone: new FormControl(data.officePhone || null),
      mobilePhone: new FormControl(data.mobilePhone || null),
      faxNumber: new FormControl(data.faxNumber || null),
      email: new FormControl(data.email || null, [Validators.required, Validators.email]),
      experience: new FormControl(data.experience || null),
      officeEmail: new FormControl(data.officeEmail || null, Validators.email),
      facebookUrl: new FormControl(data.facebookUrl || null),
      linkedinUrl: new FormControl(data.linkedinUrl || null),
      youtubeUrl: new FormControl(data.youtubeUrl || null),
      twitterUrl: new FormControl(data.twitterUrl || null),
      websiteUrl: new FormControl(data.websiteUrl || null),
      practiceAreas: new FormControl(data.practiceAreas || []),
      education: this.createEducationFormArray(data.education || []),
      practiceName: new FormControl(data.practiceName || []),
    });
  }

  createEmployeeAdminProfileFormGroup(data?) {
    return this.profileFormGroup = new FormGroup({
      firstName: new FormControl(data.firstName || null, Validators.required),
      middleName: new FormControl(data.middleName || null),
      lastName: new FormControl(data.lastName || null, Validators.required),
      officeAddress: this.createAddressFormGroup(data && data.officeAddress || null),
      officePhone: new FormControl(data.officePhone || null),
      mobilePhone: new FormControl(data.mobilePhone || null),
      faxNumber: new FormControl(data.faxNumber || null),
      email: new FormControl(data.email || null, [Validators.required, Validators.email]),
    });
  }

  createEmployeeNonAdminFormGroup(data?) {
    return this.profileFormGroup = new FormGroup({
      firstName: new FormControl(data.firstName || null, Validators.required),
      middleName: new FormControl(data.middleName || null),
      lastName: new FormControl(data.lastName || null, Validators.required),
      officeAddress: this.createAddressFormGroup(data && data.officeAddress || null),
      officePhone: new FormControl(data.officePhone || null),
      mobilePhone: new FormControl(data.mobilePhone || null),
      faxNumber: new FormControl(data.faxNumber || null),
      email: new FormControl(data.email || null, [Validators.required, Validators.email]),
      workingHours: this.createHoursFormArray(data.workingHours || []),
      languages: new FormControl(data.languages || []),
      lawFirmName: new FormControl(data.lawFirmName || null),
    });
  }

  createLawFirmFormGroup(data?) {
    return this.profileFormGroup = new FormGroup({
      id: new FormControl(data.id || null),
      name: new FormControl(data.name || null),
      organizationType: new FormControl(data.organizationType || null),
      summary: new FormControl(data.summary || null),
      awards: new FormControl(data.awards || null),
      experience: new FormControl(data.experience || null),
      officeAddress: this.createAddressFormGroup(data.officeAddress || null),
      officePhone: new FormControl(data.officePhone || null),
      mobilePhone: new FormControl(data.mobilePhone || null),
      faxNumber: new FormControl(data.faxNumber || null),
      email: new FormControl(data.email || null),
      facebookUrl: new FormControl(data.facebookUrl || null),
      linkedinUrl: new FormControl(data.linkedinUrl || null),
      twitterUrl: new FormControl(data.twitterUrl || null),
      youtubeUrl: new FormControl(data.youtubeUrl || null),
      websiteUrl: new FormControl(data.websiteUrl || null),
      yearFounded: new FormControl(data.yearFounded || null),
      organizationId: new FormControl(data.organizationId || null),
      languages: new FormControl(data.languages || []),
      practiceAreas: new FormControl(data.practiceAreas || []),
      workingHours: this.createHoursFormArray(data.workingHours || []),
    });
  }

  createUserFormGroup(data?) {
    return this.profileFormGroup = new FormGroup({
      id: new FormControl(data.id || null),
      firstName: new FormControl(data.firstName || null),
      middleName: new FormControl(data.middleName || null),
      lastName: new FormControl(data.lastName || null),
      mobilePhone: new FormControl(data.mobileNumber || null),
      faxNumber: new FormControl(data.faxNumber || null),
      workNumber: new FormControl(data.workNumber || null),
      homeNumber: new FormControl(data.homeNumber || null),
      email: new FormControl(data.email || null),
      officeAddress: this.createAddressFormGroup(data.homeAddress || null),
    });
  }

  createAddressFormGroup(data?) {
    return new FormGroup({
      city: new FormControl(data && data.city || null),
      country: new FormControl(data && data.country || null),
      line1: new FormControl(data && data.line1 || null),
      line2: new FormControl(data && data.line2 || null),
      postalCode: new FormControl(data && data.postalCode || null),
      province: new FormControl(data && data.province || null),
      state: new FormControl(data && data.state || null),
      zipCode: new FormControl(data && data.zipCode || null),
    });
  }

  createEducationFormArray(educationList: Education[]) {
    const educationControlsArray = [];
    educationList.map((education) => {
        const educationFormGroup = this.createEducationFormGroup(education);
        educationControlsArray.push(educationFormGroup);
      }
    );
    for (let i = educationList.length; i < 4; i++) {
      const educationFormGroup = this.createEducationFormGroup();
      educationControlsArray.push(educationFormGroup);
    }
    return new FormArray(educationControlsArray);
  }

  createEducationFormGroup(data?: Education) {
    return new FormGroup({
      school: new FormControl(data ? data.school : null),
      degree: new FormControl(data ? data.degree : null),
      year: new FormControl(data ? data.year : null),
      honors: new FormControl(data ? data.honors : null),
      id: new FormControl(data ? data.id : null),
    });
  }

  resetFormGroup() {
    if (this.profileFormGroup) {
      this.profileFormGroup.reset();
    }
  }

  resetProfileFormGroup(initialData?) {
    this.profileFormGroup.patchValue({...initialData, officeAddress: this.createAddressFormGroup(initialData.officeAddress).value});

    if (this.profileFormGroup.value.licensedRegions) {
      this.profileFormGroup.setControl('licensedRegions', this.createLicensedFormArray(initialData.licensedRegions));
    }
    if (this.profileFormGroup.value.education) {
      this.profileFormGroup.setControl('education', this.createEducationFormArray(initialData.education));
    }
    if (this.profileFormGroup.value.workingHours) {
      this.profileFormGroup.setControl('workingHours', this.createHoursFormArray(initialData.workingHours));
    }
  }

  createLicensedFormArray(licensedRegions: LicensedRegions[]) {
    const licensedControlsArray = licensedRegions.map((region) => this.createLicensedFormGroup(region));
    return new FormArray(licensedControlsArray);
  }

  createLicensedFormGroup(data?: LicensedRegions) {
    return new FormGroup({
      barNumber: new FormControl(data && data.barNumber ? data.barNumber : null, Validators.required),
      dateLicensed: new FormControl(data && data.dateLicensed ? data.dateLicensed : null, {
        validators: [
          Validators.pattern(DATE_PATTERN),
          Validators.required,
        ]
      }),
      id: new FormControl(data && data.id ? data.id : null),
      state: new FormControl(data && data.state ? data.state : null, Validators.required),
    });
  }

  createHoursFormArray(workingHours) {
    const workingHoursControlsArray = this.workingDays.map((day) => {
      const foundedDay = workingHours.find((hour) => day.value === hour.dayOfWeek);
      const hoursFormGroup = this.createHoursFormGroup(foundedDay || {
        dayOfWeek: day.value,
        start: {hour: null, minutes: null},
        end: {hour: null, minutes: null},
      });
      return hoursFormGroup;
    });
    return new FormArray(workingHoursControlsArray);
  }

  createHoursFormGroup(data) {
    return new FormGroup({
      dayOfWeek: new FormControl(data.dayOfWeek),
      start: new FormControl(data.start || null),
      end: new FormControl(data.end || null),
    });
  }

}
