import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { ActivatedRoute } from '@angular/router';
import { merge } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'app-individual-client-search',
  templateUrl: 'individual-client-search.component.html',
})
@DestroySubscribers()
export class IndividualClientSearchComponent implements OnInit, OnDestroy, AddSubscribers {
  form = new FormGroup({
    easyVisaId: new FormControl(null, Validators.pattern(/(^[A-Z]\d{10}$)/)),
    mobileNumber: new FormControl(null),
    lastName: new FormControl(null),
  });

  private subscribers: any = {};

  constructor(
    private activeModal: NgbActiveModal,
    private route: ActivatedRoute,
  ) {
  }

  get evIdFormControl() {
    return this.form.get('easyVisaId');
  }

  get mobileFormControl() {
    return this.form.get('mobileNumber');
  }

  get lastNameFormControl() {
    return this.form.get('lastName');
  }

  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
  }

  addSubscribers() {
    this.subscribers.paramsSubscription = this.route.queryParams.subscribe(
      (params) => this.form.patchValue({
        easyVisaId: params.easyVisaId || null,
        mobileNumber: params.mobileNumber || null,
        lastName: params.lastName || null,
      })
    );
    this.subscribers.formChangeSubscription = merge(
      this.evIdFormControl.valueChanges.pipe(
        filter(v => !!v),
        map(() => ({ mobileNumber: null, lastName: null }))
      ),
      this.mobileFormControl.valueChanges.pipe(
        filter(v => !!v),
        map(() => ({ lastName: null, easyVisaId: null }))
      ),
      this.lastNameFormControl.valueChanges.pipe(
        filter(v => !!v),
        map(() => ({ mobileNumber: null, easyVisaId: null }))
      ),
    ).subscribe((value) => this.form.patchValue(value));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  submit() {
    this.activeModal.close(this.form.value);
  }
}
