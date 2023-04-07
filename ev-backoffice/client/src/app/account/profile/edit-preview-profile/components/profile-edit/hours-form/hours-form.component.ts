import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { workingDays } from '../../../models/working-days';

import { ProfileEditService } from '../profile-edit.service';

@Component({
  selector: 'app-hours-form',
  templateUrl: './hours-form.component.html',
  styleUrls: ['./hours-form.component.scss'],
})

export class HoursFormComponent {
  workingDays = workingDays;

  get workingHoursFormGroups() {
    return this.profileEditService.workingHoursFormArray.controls as FormGroup[];
  }

  constructor(
    private profileEditService: ProfileEditService,
  ) {

  }

}
