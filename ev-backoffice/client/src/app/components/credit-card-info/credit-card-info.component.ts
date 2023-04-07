import { AfterViewInit, Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { months } from '../../core/models/months';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';
import { FattService, ModalService } from '../../core/services';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-credit-card-info',
  templateUrl: './credit-card-info.component.html',
  styleUrls: ['./credit-card-info.component.scss'],

})
export class CreditCardInfoComponent implements OnInit, AfterViewInit {

  @Input() formGroup: FormGroup;
  @Input() submitted = false;
  @Input() colHeadNumber = false;
  @ViewChild('cvvModal', { static: true }) cvvModal;

  fattIsValidSubject$: Observable<boolean>;

  months = months;
  years = [];

  constructor(
    private modalService: ModalService,
    private fattService: FattService,
  ) {

  }

  get firstNameControl() {
    return this.formGroup.get('firstname');
  }

  get lastNameControl() {
    return this.formGroup.get('lastname');
  }

  get expirationMonthControl() {
    return this.formGroup.get('month');
  }

  get expirationYearControl() {
    return this.formGroup.get('year');
  }

  ngOnInit() {
    this.createYearsList();
    this.fattIsValidSubject$ = this.fattService.fattIsValidSubject$;
  }

  ngAfterViewInit() {
    this.showCardForm();
  }

  createYearsList() {
    const currentYear = new Date().getFullYear();
    for (let i = currentYear; i < currentYear + 10; i++) {
      this.years.push(i);
    }
  }
  showCardForm() {
  const dataCard = {
    number: {
      id: 'card-number',
      placeholder: '',
      style: 'height: 100%; width: 100%; color: #495057; font-size: 12.25px;' +
        ' font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica' +
        ' Neue", Arial, "Noto Sans", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";',
    },
    cvv: {
      id: 'card-cvv',
      placeholder: '000',
      style: 'height: 100%; width: 100%; color: #495057; font-size: 12.25px;' +
        ' font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica' +
        ' Neue", Arial, "Noto Sans", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";',
    },
  };
  this.fattService.showCardForm(dataCard);
  this.fattService.getIsUnValidFattForm();
  this.fattService.getIsValidFattForm();
}

  openCvvModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.CVV_MODAL.HEADER',
      body: this.cvvModal,
      buttons,
      size: 'lg'
    });
  }
}
