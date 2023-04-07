import { Component, Input, OnInit } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { countries } from '../../../../../core/models/countries';

@Component({
  selector: 'app-beneficiary-countries-modal',
  templateUrl: 'beneficiary-countries-modal.component.html',
  styleUrls: ['beneficiary-countries-modal.component.scss']
})
export class BeneficiaryCountriesModalComponent implements OnInit {
  @Input() countriesFormControl;
  @Input() countries;
  beneficiaryCountries = countries;

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  ngOnInit() {
    this.resetCheckboxes();
    if (this.countriesFormControl.value) {
      this.countriesFormControl.value.map((selectedCountry) => {
        const foundedCountry: { label: string; value: string; checked?: boolean } = this.beneficiaryCountries.find(
          (country) => country.value === selectedCountry
        );
        if (foundedCountry) {
          foundedCountry.checked = true;
        }
      });
    }
  }

  clearAllCountries() {
    this.resetCheckboxes();
    this.countriesFormControl.reset();
  }

  resetCheckboxes() {
    this.beneficiaryCountries.forEach((country: { label: string; value: string; checked: boolean }) => country.checked = false);
  }

  addSelectedCountries() {
    const selectedCountries = this.beneficiaryCountries.filter(
      (country: { label: string; value: string; checked: boolean }) => country.checked
    );
    this.activeModal.close(selectedCountries || null);
  }

}
