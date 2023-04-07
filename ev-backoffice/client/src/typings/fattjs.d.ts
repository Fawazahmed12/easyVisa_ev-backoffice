// Typings reference file, you can add your own global typings here
// https://www.typescriptlang.org/docs/handbook/writing-declaration-files.html

export class Address {
  address_1: string;
  address_2?: string;
  address_city: string;
  address_state: string;
  address_zip: string;
  address_country: string;
}

export class ExtraDetails extends Address {
  firstname: string;
  lastname: string;
  month: string;
  year: string;
  phone: string;
  url: string;
  validate?: boolean;
  customer_id?: string;
}

export class CustomerData extends Address {
  allow_invoice_credit_card_payments: boolean;
  cc_emails?: string;
  cc_sms?: string;
  company: string;
  created_at: string;
  deleted_at?: string;
  email?: string;
  firstname: string;
  gravatar: boolean;
  id: string;
  lastname: string;
  notes?: string;
  options?: string;
  phone: string;
  reference?: string;
  updated_at: string;
}

export interface TokenizeData extends Address {
  bank_holder_type?: string;
  bank_name?: string;
  bank_type?: string;
  card_exp: string;
  card_exp_datetime: string;
  card_last_four: string;
  card_type: string;
  created_at: string;
  customer: CustomerData;
  customer_id: string;
  has_cvv: boolean;
  id: string;
  is_default: number;
  is_usable_in_vt: boolean;
  method: string;
  nickname: string;
  person_name: string;
  updated_at: string;
}

export interface CardProperties {
  id: string;
  placeholder?: string;
  style?: string;
}

declare global {
  export class FattJs {
    showCardForm: () => Promise<any>;
    tokenize: (extraDetails: ExtraDetails) => Promise<TokenizeData>;
    on: (event: string, specDefinitions: () => void) => void;
    validCvv?: boolean;
    validNumber?: boolean;

    constructor(webPaymentsToken: string, options: { number: CardProperties; cvv: CardProperties });
  }
}

