import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { degree } from '../../../models/degree.model';
import { honor } from '../../../models/honor.model';

import { ProfileEditService } from '../profile-edit.service';

@Component({
  selector: 'app-education-form',
  templateUrl: './education-form.component.html',
})

export class EducationFormComponent implements OnInit {

  degrees = degree;
  honors = honor;
  years = [];

  get educationFormGroups() {
    return this.profileEditService.educationFormArray.controls as FormGroup[];
  }

  constructor(
    private profileEditService: ProfileEditService,
  ) {

  }

  ngOnInit() {
    this.getYearsList();
  }

  getYearsList() {
    const currentYear = new Date().getFullYear();
    for (let i = 1950; i <= currentYear; i++) {
      this.years.push(i);
    }
  }
}
