import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormGroup, } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { combineLatest, ReplaySubject } from 'rxjs';

@Component({
  selector: 'app-fee-item',
  templateUrl: './fee-item.component.html',
  styleUrls: [ './fee-item.component.scss' ]
})
@DestroySubscribers()
export class FeeItemComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() chargeFormGroup: FormGroup;
  @Input() index = null;
  @Input() form;

  @Output() removeFeeRow = new EventEmitter();

  extValueSubject$: ReplaySubject<number> = new ReplaySubject<number>(1);
  private subscribers: any = {};

  get descriptionFormControl() {
    return this.chargeFormGroup.get('description');
  }

  get eachFormControl() {
    return this.chargeFormGroup.get('each');
  }

  get quantityFormControl() {
    return this.chargeFormGroup.get('quantity');
  }


  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
  }

  addSubscribers() {
    this.subscribers.applicantsFormArraySubscription = combineLatest([
      this.eachFormControl.valueChanges,
      this.quantityFormControl.valueChanges
    ]).subscribe(() => {
        this.extValueSubject$.next(this.eachFormControl.value * this.quantityFormControl.value);
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  removeFee(index) {
    this.removeFeeRow.emit(index);
  }
}
