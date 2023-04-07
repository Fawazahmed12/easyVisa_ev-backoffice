import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { map } from 'rxjs/operators';

import { EstimatedTax } from '../../models/estimated-tax.model';
import { TaxTypes } from '../../models/tax-types.enum';
import { TaxAddress } from '../../models/tax-address.model';


@Injectable()
export class TaxesRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  estimatedTaxPostRequest(data: {type: TaxTypes; address?: TaxAddress; packageId?: string}) {
    return this.httpClient.post<EstimatedTax>(`taxes`, data).pipe(
      map(estimatedTax => ({
        estimatedTax,
        taxType: data.type,
      }))
    );
  }
}
