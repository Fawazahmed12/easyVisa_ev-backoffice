import { Component, Input } from '@angular/core';
import { PaymentMethodDetails } from '../../core/models/payment-method-details.model';


@Component({
  selector: 'app-credit-card-info-view',
  templateUrl: './credit-card-info-view.component.html',
  styleUrls: ['./credit-card-info-view.component.scss'],

})
export class CreditCardInfoViewComponent {
  @Input() paymentMethod: PaymentMethodDetails = null;

  getFaCardLogo() {
    switch (this.paymentMethod.cardType) {
      case 'Visa': {
        return 'fa-cc-visa';
      }
      case 'Mastercard': {
        return 'fa-cc-mastercard';
      }
      case 'Americanexpress': {
        return 'fa-cc-amex';
      }

      default: {
        return '';
      }
    }
  }
}
