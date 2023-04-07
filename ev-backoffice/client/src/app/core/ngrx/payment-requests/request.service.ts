import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { map } from 'rxjs/operators';

import { PaymentMethodDetails } from '../../models/payment-method-details.model';
import { AccountTransaction } from '../../models/accountTransaction.model';
import { EstimatedTax } from '../../models/estimated-tax.model';


@Injectable()
export class PaymentRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  paymentMethodPutRequest(data) {
    return this.httpClient.put<PaymentMethodDetails>(`/users/${data.userId}/payment-method`, {...data.fmPaymentMethod});
  }

  paymentMethodGetRequest(id) {
    return this.httpClient.get<PaymentMethodDetails>(`/users/${id}/payment-method`);
  }

  balanceGetRequest(data: {payload: string; type: string}) {
    return this.httpClient.get<EstimatedTax>(`/account-transactions/user/${data.payload}/balance`).pipe(
      map((res) => ({
          balance: res,
          actionType: data.type
        }))
    );
  }

  accountTransactionsGetRequest(data): any {
    return this.httpClient.get<HttpResponse<AccountTransaction[]>>(
      `/account-transactions/user/${data.id}`,
      {params: data.params, observe: 'response'}
      ).pipe(
      map(({body, headers}) => {
        const xTotalCount = headers.get('x-total-count');
        return {body, xTotalCount};
      })
    );
  }

  accountTransactionPostRequest(data) {
    return this.httpClient.post<AccountTransaction>(`/account-transactions/user/${data.id}`, {...data.accountTransaction});
  }

  payBalancePostRequest(data) {
    return this.httpClient.post<any>(`/account-transactions/user/${data.id}/payment`, {balance: -data.balance});
  }
}
