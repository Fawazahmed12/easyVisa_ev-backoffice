import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { catchError, switchMap } from 'rxjs/operators';
import { EMPTY, Subject } from 'rxjs';

import { ModalService } from '../../../../../../core/services';
import { languages } from '../../../../../../core/models/languages';
import {
  ConfirmButtonType,
} from '../../../../../../core/modals/confirm-modal/confirm-modal.component';
import { ProfileEditService } from '../profile-edit.service';


@Component({
  selector: 'app-languages-form',
  templateUrl: './languages-form.component.html',
  styleUrls: ['./languages-form.component.scss'],
})
@DestroySubscribers()
export class LanguagesFormComponent implements OnInit, OnDestroy, AddSubscribers {
  @ViewChild('selectLanguages', { static: true }) selectLanguages;
  languagesProfile = languages;
  private selectLanguagesSubject$: Subject<any> = new Subject();

  private subscribers: any = {};

  get languagesFormControl() {
    return this.profileEditService.profileFormGroup.get('languages');
  }

  constructor(
    private modalService: ModalService,
    private profileEditService: ProfileEditService,
  ) {

  }

  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
  }

  addSubscribers() {
    this.subscribers.selectSubscription = this.selectLanguagesSubject$.pipe(
      switchMap(() => this.openSelectLanguagesSpoken().pipe(
        catchError(() => EMPTY
        )
      )),
    ).subscribe(() => {
        if (this.languagesProfile.length) {
          const res = [];
          this.languagesProfile.map((language) => {
            if (language.checked) {
              res.push(language.value);
            }
          });
          this.profileEditService.profileFormGroup.patchValue({languages: res});
          this.profileEditService.profileFormGroup.get('languages').markAsDirty();
        }
      }
    );
  }

  selectLanguage(event, language) {
    language.checked = event.target.checked;
  }

  selectLanguagesProfile() {
    this.resetCheckboxes();
    this.setCheckboxes();
    this.selectLanguagesSubject$.next(true);
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  openSelectLanguagesSpoken() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.CONFIRM',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.SELECT_LANGUAGES',
      body: this.selectLanguages,
      buttons,
      centered: true,
      size: 'lg',
    });
  }

  resetCheckboxes() {
    this.languagesProfile.forEach((language) => language.checked = false);
  }

  setCheckboxes() {
    this.languagesFormControl.value.map((value) => {
      const foundedLanguage = this.languagesProfile.find(
        (language) => language.value === value
      );
      if (foundedLanguage) {
        foundedLanguage.checked = true;
      }
    });
  }
}
