import { Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

import { DestroySubscribers } from 'ngx-destroy-subscribers';
import { filter, map } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { Package } from '../../core/models/package/package.model';
import { PackagesService } from '../../core/services';
import { PackageStatus } from '../../core/models/package/package-status.enum';


@Component({
  selector: 'app-select-package',
  templateUrl: './select-package.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectPackageComponent),
      multi: true
    }
  ]
})
@DestroySubscribers()
export class SelectPackageComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() withBlockedPackages = false;
  @Input() withLeadPackages = false;
  @Input() withTransferredPackages = false;
  @Input() withDeletedPackages = false;
  @Input() showPackageStatus = false;
  @Input() showPackageCategories = false;

  formControl = new FormControl();
  private subscribers: any = {};
  packages$: Observable<Package[]>;

  onChange: (value: string) => void;
  onTouched: () => void;

  constructor(
    private packagesService: PackagesService,
  ) {
  }

  ngOnInit() {

    let validStatusList = [ PackageStatus.OPEN, PackageStatus.CLOSED ];

    if (this.withBlockedPackages) {
      validStatusList = [ ...validStatusList, PackageStatus.BLOCKED ];
    }
    if (this.withLeadPackages) {
      validStatusList = [ ...validStatusList, PackageStatus.LEAD ];
    }
    if (this.withTransferredPackages) {
      validStatusList = [ ...validStatusList, PackageStatus.TRANSFERRED ];
    }
    if (this.withDeletedPackages) {
      validStatusList = [ ...validStatusList, PackageStatus.DELETED ];
    }

    this.packages$ = this.packagesService.packages$.pipe(
      map((packages) => packages.filter((item) => validStatusList.includes(item.status)))
    );

  }

  addSubscribers() {
    this.subscribers.formControlSubscription = this.formControl.valueChanges.pipe(
      filter(() => !!this.onChange)
    ).subscribe((value) => this.onChange(value));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  writeValue(value) {
    this.formControl.patchValue(value, { emitEvent: false });
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    isDisabled ? this.formControl.disable({ emitEvent: false })
      : this.formControl.enable({ emitEvent: false });
  }
}
