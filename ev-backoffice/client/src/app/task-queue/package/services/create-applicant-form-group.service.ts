import { Injectable } from '@angular/core';
import { FormArray, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

import { Subject } from 'rxjs';
import { isEqual } from 'lodash-es';

import { PackageApplicant } from '../../../core/models/package/package-applicant.model';
import { Package } from '../../../core/models/package/package.model';
import { DATE_PATTERN } from '../../../shared/validators/constants/date-pattern.const';
import { getFullDate } from '../../../shared/utils/get-full-date';
import { ApplicantType } from '../../../core/models/applicantType.enum';
import { EMAIL_PATTERN } from '../../../shared/validators/constants/email-pattern.const';

import { requiredForPetitionerValidator } from '../create-edit-package/validators/required-for-petitioner.validator';
import { requiredApplicantEmail } from '../create-edit-package/validators/applicant-email-required.validator';


@Injectable()
export class CreateApplicantFormGroupService {

  private resetFormGroup$: Subject<void> = new Subject();
  submittedSubject$: Subject<boolean> = new Subject();
  packageSnapshot: Package | string = 'NEW';
  formGroup: FormGroup;
  canOut = false;

  constructor() {
    this.resetFormGroup$
    .subscribe(() => {
      this.formGroup.setControl(
        'applicants',
        new FormArray([this.createBeneficiaryFormGroup(ApplicantType.PETITIONER, null)])
      );
    });
  }

  createFormGroupSnapShot(formGroupValue) {
    this.packageSnapshot = formGroupValue;
  }

  checkFormGroupChanges(formGroup): boolean {
    return !isEqual(this.packageSnapshot, formGroup);
  }

  createFormGroup(data?: Package) {
    this.formGroup = new FormGroup({
      id: new FormControl({value: data && data.id, disabled: !data}),
      representativeId: new FormControl(data && data.representative.id ? data.representative.id : null),
      applicants: new FormArray(
        data && data.applicants.length ? [] : [this.createBeneficiaryFormGroup(ApplicantType.PETITIONER, null)]),
    });
    const {representativeId, ...valueToSnapshot} = this.formGroup.getRawValue();
    this.packageSnapshot = valueToSnapshot;
  }

  createBeneficiaryFormGroup(applicantType, data?: PackageApplicant) {
    return new FormGroup({
        inviteApplicant: new FormControl({
            value: data && data.inviteApplicant ? data.inviteApplicant : false,
            disabled: data && !data.profile.email || !data,
          }
        ),
        optIn: new FormControl(data ? data.optIn : null),
        register: new FormControl(data ? data.register : null),
        inOpenPackage: new FormControl(data ? data.inOpenPackage : null),
        inBlockedPackage: new FormControl(data ? data.inBlockedPackage : null),
        citizenshipStatus: new FormControl(data ? data.citizenshipStatus : null),
        benefitCategory: new FormControl(data ? data.benefitCategory : '', {
          validators: [this.benefitCategoryValidator()]
        }),
        fee: new FormControl(data ? data.fee : null),
        applicantType: new FormControl(data ? data.applicantType : applicantType),
        profile: this.createProfileFormGroup(
          data ?
            data.profile :
            applicantType === ApplicantType.PETITIONER ?
              {homeAddress: {country: 'UNITED_STATES'}} :
              null
        ),
      },
      {
        validators: [
          requiredApplicantEmail(
            'profile',
            'email',
            'inviteApplicant',
            'applicantType'
          ),
        ],
      }
    );
  }

  createProfileFormGroup(data?, isRequiredEmail?, isRequiredMinAge?) {
    return new FormGroup({
        id: new FormControl(data ? data.id : null),
        firstName: new FormControl(data ? data.firstName : null, Validators.required),
        middleName: new FormControl(data ? data.middleName : null),
        lastName: new FormControl(data ? data.lastName : null, Validators.required),
        easyVisaId: new FormControl(data ? data.easyVisaId : null),
        email: new FormControl(data ? data.email : null, {
          validators: [
            Validators.email,
            Validators.pattern(EMAIL_PATTERN),
          ],
          updateOn: 'change',
        }),
        isEmailVerified: new FormControl(true, Validators.required),
        dateOfBirth: new FormControl(data ? data.dateOfBirth : null, {
          validators: [
            this.dateOfBirthValidator(isRequiredMinAge),
            Validators.pattern(DATE_PATTERN),
          ],
        }),
        homeAddress: new FormGroup({
          city: new FormControl(data && data.homeAddress ? data.homeAddress.city : null),
          country: new FormControl(data && data.homeAddress ? data.homeAddress.country : null),
          line1: new FormControl(data && data.homeAddress ? data.homeAddress.line1 : null),
          line2: new FormControl(data && data.homeAddress ? data.homeAddress.line2 : null),
          postalCode: new FormControl(data && data.homeAddress ? data.homeAddress.postalCode : null),
          province: new FormControl(data && data.homeAddress ? data.homeAddress.province : null),
          state: new FormControl(data && data.homeAddress ? data.homeAddress.state : null),
          zipCode: new FormControl(data && data.homeAddress ? data.homeAddress.zipCode : null),
        }),
        mobileNumber: new FormControl(data ? data.mobileNumber : null),
        homeNumber: new FormControl(data ? data.homeNumber : null),
        workNumber: new FormControl(data ? data.workNumber : null),
      },
      {
        validators: [
          requiredForPetitionerValidator('email', isRequiredEmail),
        ],
      });
  }

  resetFormGroup() {
    this.resetFormGroup$.next();
  }

  getRequiredAges(petitionerMinAgeConfig = 18, applicantMaxAgeConfig = 120) {
    const currentDate: Date = new Date();
    const petitionerMinAge: Date = new Date(new Date().setUTCFullYear(currentDate.getUTCFullYear() - petitionerMinAgeConfig));
    const applicantMaxAge: Date = new Date(new Date().setUTCFullYear(currentDate.getUTCFullYear() - applicantMaxAgeConfig));
    return {
      beneficiaryMinAge: getFullDate(currentDate),
      petitionerMinAge: getFullDate(petitionerMinAge),
      applicantMaxAge: getFullDate(applicantMaxAge)
    };
  }

  dateOfBirthValidator(isRequiredMinAge): ValidatorFn {
    return ({value}: FormControl): ValidationErrors | null => {
      if (value) {
        const date = value.replace(/-/g, ',');
        const applicantAges = this.getRequiredAges();
        const selectedDOB = getFullDate(new Date(date));
        return selectedDOB > applicantAges.beneficiaryMinAge
        || selectedDOB < applicantAges.applicantMaxAge
        || (isRequiredMinAge && selectedDOB > applicantAges.petitionerMinAge)
          ? {invalidDOB: true} : null;
      }
      return null;
    };
  }

  benefitCategoryValidator(): ValidatorFn {
    return ({value}: FormControl): ValidationErrors | null => {
      if (value) {
        return value === '' ? {invalidBenefitCategory: true} : null;
      }
      return null;
    };
  }
}
