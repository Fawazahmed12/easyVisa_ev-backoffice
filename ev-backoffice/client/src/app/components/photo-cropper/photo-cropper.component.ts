import { Component } from '@angular/core';

import { base64ToFile, ImageCroppedEvent, ImageTransform } from 'ngx-image-cropper';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ReplaySubject, Subject } from 'rxjs';


@Component({
  selector: 'app-photo-cropper',
  templateUrl: './photo-cropper.component.html',
  styleUrls: ['./photo-cropper.component.scss']
})
export class PhotoCropperComponent {
  imageChangedEvent: any = '';
  fileName = '';
  fileNameSubject$: Subject<string> = new Subject<string>();
  croppedImage: any = '';
  canvasRotation = 0;
  rotation = 0;
  scale = 1;
  showCropper = false;
  containWithinAspectRatio = false;
  transform: ImageTransform = {};
  minWidth = 120;
  minHeight = 150;
  tooSmallWidthSubject$: ReplaySubject<boolean> = new ReplaySubject<boolean>(1);

  constructor(
    private activeModal: NgbActiveModal,
  ) {}

  fileChangeEvent(event: any): void {
    this.imageChangedEvent = event;
    this.fileName = event.target.files[0].name;
    this.fileNameSubject$.next(this.fileName);
  }

  imageCropped(event: ImageCroppedEvent) {
    this.croppedImage = event.base64;
    this.tooSmallWidthSubject$.next(event.width < this.minWidth || event.height < this.minHeight);
  }

  imageLoaded() {
    this.showCropper = true;
  }

  rotateLeft() {
    this.canvasRotation--;
    this.flipAfterRotate();
  }

  rotateRight() {
    this.canvasRotation++;
    this.flipAfterRotate();
  }

  private flipAfterRotate() {
    const flippedH = this.transform.flipH;
    const flippedV = this.transform.flipV;
    this.transform = {
      ...this.transform,
      flipH: flippedV,
      flipV: flippedH
    };
  }

  flipHorizontal() {
    this.transform = {
      ...this.transform,
      flipH: !this.transform.flipH
    };
  }

  flipVertical() {
    this.transform = {
      ...this.transform,
      flipV: !this.transform.flipV
    };
  }

  resetImage() {
    this.scale = 1;
    this.rotation = 0;
    this.canvasRotation = 0;
    this.transform = {};
  }

  zoomOut() {
    this.scale -= 0.1;
    this.transform = {
      ...this.transform,
      scale: this.scale
    };
  }

  zoomIn() {
    this.scale += 0.1;
    this.transform = {
      ...this.transform,
      scale: this.scale
    };
  }

  closeModal() {
    this.activeModal.close({file: base64ToFile(this.croppedImage), fileName: this.fileName});
  }
}
