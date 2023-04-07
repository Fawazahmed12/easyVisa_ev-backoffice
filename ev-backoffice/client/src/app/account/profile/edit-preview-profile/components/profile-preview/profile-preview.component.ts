import { Component, Input, OnInit } from '@angular/core';

import { languages } from '../../../../../core/models/languages';
import { practice } from '../../models/practice.model';
import { workingDays } from '../../models/working-days';
import { honor } from '../../models/honor.model';
import { degree } from '../../models/degree.model';
import { NumberOfYearsPipe } from '../profile-edit/license-form/pipes/number-of-years.pipe';
import { OrganizationProfile } from '../../models/organization-profile.model';
import { AttorneyProfile } from '../../models/attorney-profile.model';


@Component({
  selector: 'app-profile-preview',
  templateUrl: './profile-preview.component.html',
  styleUrls: ['./profile-preview.component.scss'],
})

export class ProfilePreviewComponent implements OnInit {
  @Input() profile: OrganizationProfile & AttorneyProfile;
  @Input() isOrganizationProfile: boolean;
  @Input() photoUrl: string;
  languagesProfile = languages;
  practices = practice;
  workingDays = workingDays;
  honors = honor;
  degree = degree;
  yearLicensed: any;
  contactInfo = [];
  socialUrls = [];

  get currentYear() {
    return new Date().getFullYear();
  }

  constructor(
    private numberOfYearsPipe: NumberOfYearsPipe,
  ) {
  }

  ngOnInit() {
    this.setSocialUrls(this.profile);
    this.setContactInfo(this.profile);
  }

  setSocialUrls(profile) {
    this.socialUrls = [
      {
        socialUrl: profile && profile.facebookUrl ? `https://${profile.facebookUrl}` : null,
        style: 'facebook',
      },
      {
        socialUrl: profile && profile.youtubeUrl ? `https://${profile.youtubeUrl}` : null,
        style: 'youtube',
      },
      {
        socialUrl: profile && profile.linkedinUrl ? `https://${profile.linkedinUrl}` : null,
        style: 'linkedin',
      },
      {
        socialUrl: profile && profile.twitterUrl ? `https://${profile.twitterUrl}` : null,
        style: 'twitter',
      },
    ];
  }

  setContactInfo(profile) {
    if (!this.isOrganizationProfile) {
      this.contactInfo = [
        {
          number: profile && profile.officePhone,
          label: 'FORM.LABELS.OFFICE',
        },
        {
          number: profile && profile.mobilePhone,
          label: 'FORM.LABELS.MOBILE',
        },
        {
          number: profile && profile.faxNumber,
          label: 'FORM.LABELS.FAX',
        },
        {
          number: profile && profile.email,
          label: 'FORM.LABELS.EMAIL',
        },
      ];
    } else {
      this.contactInfo = [
        {
          number: profile && profile.officePhone,
          label: 'FORM.LABELS.OFFICE',
        },
        {
          number: profile && profile.faxNumber,
          label: 'FORM.LABELS.FAX',
        }
      ];
    }
  }

  transformLicence(value) {
    const licences = value.licensedRegions;
    if (licences.length) {
      this.yearLicensed = 0;
      licences.map((license) => {
          const numberLicensedYears = this.numberOfYearsPipe.transform(license.dateLicensed);
          if (numberLicensedYears === '<1') {
            return;
          } else if (this.yearLicensed < numberLicensedYears) {
            this.yearLicensed = numberLicensedYears;
          }
        }
      );
      if ((this.yearLicensed === 0)) {
        this.yearLicensed = '<1';
      }
    }
    return this.yearLicensed;
  }
}
