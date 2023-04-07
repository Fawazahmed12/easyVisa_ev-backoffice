import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, switchMap, tap, withLatestFrom } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { checkAllowedFileType } from '../../../../../../shared/utils/check-allowed-file-type';
import { validateFileSize } from '../../../../../../shared/utils/validate-file-size';
import { FileTypeBrowser } from '../../../../../../core/models/file-type-browser.enum';
import { ConfirmButtonType, OkButton, OkButtonLg } from '../../../../../../core/modals/confirm-modal/confirm-modal.component';
import { ModalService, UserService } from '../../../../../../core/services';
import { EditPreviewProfileService } from '../../../edit-preview-profile.service';
import { User } from '../../../../../../core/models/user.model';
import { AttorneyProfile } from '../../../models/attorney-profile.model';
import { Profile } from '../../../../../../core/models/profile.model';
import { EmployeeProfile } from '../../../models/employee-profile.model';
import { OrganizationProfile } from '../../../models/organization-profile.model';
import { PhotoCropperComponent } from '../../../../../../components/photo-cropper/photo-cropper.component';
import { DownloadPrintComponent } from '../../../../../../documents/print-package-forms/download-print/download-print.component';
import { fromPromise } from 'rxjs/internal-compatibility';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-upload-photo',
  templateUrl: './upload-photo.component.html',
  styleUrls: ['./upload-photo.component.scss'],
})

@DestroySubscribers()
export class UploadPhotoComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() isOrganizationProfile = false;
  @ViewChild('confirmModal', { static: true }) confirmModal;

  profile$: Observable<Profile | AttorneyProfile | EmployeeProfile>;
  organizationProfile$: Observable<OrganizationProfile>;
  isFileNotAllowed$: Subject<boolean> = new Subject<boolean>();
  isFileSizeNotAllowed$: Subject<boolean> = new Subject<boolean>();
  uploadPictureSubject$: Subject<FormData> = new Subject();
  uploadPhotoSubject$: Subject<void> = new Subject();

  fileTypes = [
    FileTypeBrowser.JPG,
    FileTypeBrowser.JPG1,
    FileTypeBrowser.JPEG,
    FileTypeBrowser.JPEG1,
    FileTypeBrowser.PNG,
    FileTypeBrowser.TIFF,
    FileTypeBrowser.TIFF1,
    FileTypeBrowser.BMP,
    FileTypeBrowser.BMP1,
  ];
  acceptFileTypes = this.fileTypes.toString();

  private subscribers: any = {};

  constructor(
    private modalService: ModalService,
    private profileService: EditPreviewProfileService,
    private userService: UserService,
    private ngbModal: NgbModal,
  ) {

  }

  ngOnInit() {
    this.profile$ = this.profileService.profile$;
    this.organizationProfile$ = this.profileService.organization$;
  }

  addSubscribers() {
    this.subscribers.uploadPictureSubscription = this.uploadPictureSubject$.pipe(
      withLatestFrom(this.userService.currentUser$),
      withLatestFrom(this.organizationProfile$),
      switchMap(([[data, user], organization]: [[FormData, User], OrganizationProfile]) => {
          if (!this.isOrganizationProfile) {
            return this.profileService.uploadProfilePicture({file: data, id: user.id}).pipe(
              this.showErrorModalWithResponse(),
            );
          } else {
            return this.profileService.uploadOrganizationPicture({file: data, id: organization.id}).pipe(
              this.showErrorModalWithResponse(),
            );
          }
        }
      )
    ).subscribe();

    this.subscribers.openCropperSubscription = this.uploadPhotoSubject$.pipe(
      switchMap(() => this.openPhotoUploadModal()),
    ).subscribe(res => this.uploadPhotoAfterCropper(res));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  uploadPhotoAfterCropper(data) {
    const {file, fileName} = data;
    const isFileAllowed = checkAllowedFileType(file.type, this.fileTypes);
    this.isFileNotAllowed$.next(!isFileAllowed);
    const isFileSizeAllowed = validateFileSize(file.size);
    this.isFileSizeNotAllowed$.next(!isFileSizeAllowed);

    if (file && isFileAllowed && isFileSizeAllowed) {
      const formData: FormData = new FormData();
      formData.append('profilePhoto', file, fileName);
      this.uploadPictureSubject$.next(formData);
    }
  }

  openPhotoGuidelinesModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.PHOTO_GUIDELINES_MODAL.TITLE',
      body: this.confirmModal,
      buttons,
      windowClass: 'custom-modal-lg',
    });
  }


  openPhotoUploadModal() {
    const modalRef = this.ngbModal.open(PhotoCropperComponent, {
      // centered: true,
      size: 'lg',
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  showErrorModalWithResponse() {
    return (observable) => observable.pipe(
      catchError((error: HttpErrorResponse) => {
          if (error.status === 422) {
            this.modalService.openConfirmModal({
              header: 'TEMPLATE.ACCOUNT.PROFILE.UNSUPPORTED_FILE_TYPE.HEADER',
              body: error.error.errors,
              centered: true,
              buttons: [OkButtonLg]
            });
          } else if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [error.error]);
          }
        return EMPTY;
        }
      ),
    );
  }

  uploadPhoto() {
    this.uploadPhotoSubject$.next();
  }
}
