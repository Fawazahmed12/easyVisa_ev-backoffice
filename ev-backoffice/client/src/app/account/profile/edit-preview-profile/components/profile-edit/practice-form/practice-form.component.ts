import { Component, OnDestroy, OnInit } from '@angular/core';

import { cloneDeep } from 'lodash-es';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { practice } from '../../../models/practice.model';
import { ProfileEditService } from '../profile-edit.service';

@Component({
  selector: 'app-practice-form',
  templateUrl: './practice-form.component.html',
  styleUrls: ['./practice-form.component.scss'],
})

@DestroySubscribers()
export class PracticeFormComponent implements OnInit, OnDestroy, AddSubscribers {
  practices = cloneDeep(practice);

  private subscribers: any = {};

  get practiceFormControl() {
    return this.profileEditService.profileFormGroup.get('practiceAreas');
  }

  constructor(
    private profileEditService: ProfileEditService,
  ) {

  }

  ngOnInit() {
    this.setCheckboxes();
  }

  addSubscribers() {
    this.subscribers.formControlSubscription = this.practiceFormControl.valueChanges.subscribe(
      () => this.setCheckboxes()
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  setCheckboxes() {
    this.practices.map((currentPractice) => {
      const foundedPractice = this.practiceFormControl.value.find((value) => value === currentPractice.value);
      foundedPractice ? currentPractice.checked = true : currentPractice.checked = false;
    });
  }

  selectPractice(event, practiceCheck) {
    practiceCheck.checked = event.target.checked;
    const findPractice = [];
    this.practices.map((practiceList) => {
      if (practiceList.checked) {
        findPractice.push(practiceList.value);
      }
    });
    this.practiceFormControl.patchValue(findPractice);
    this.practiceFormControl.markAsDirty();
  }
}
