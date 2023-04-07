import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { AuthService, ModalService, UserService } from '../../core/services';
import { SharedModule } from '../../shared/shared.module';
import { AuthServiceMock, SignUpServiceMock, UserServiceMock } from '../../core/testing';
import { RepresentativeType } from '../../core/models/representativeType.enum';
import { AttorneyType } from '../../core/models/attorney-type.enum';

import { SignUpService } from '../services';

import { SelectRepresentativeTypeComponent } from './select-representative-type.component';


const routerSpy = {
  navigate: jasmine.createSpy('navigate').and.returnValue(true)
};

const modalSpy = {
  openConfirmModal: jasmine.createSpy('openConfirmModal')
};

describe('SelectRepresentativeTypeComponent', () => {
  let component: SelectRepresentativeTypeComponent;
  let fixture: ComponentFixture<SelectRepresentativeTypeComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const formDataMock = {
    id: '',
    representativeType: RepresentativeType.ATTORNEY,
    attorneyType: AttorneyType.SOLO_PRACTITIONER,
    state: 'MW',
    city: 'Cansas',
    org: 'test_data',
    name: 'Jack',
  };

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        ReactiveFormsModule,
        RouterTestingModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [SelectRepresentativeTypeComponent],
      providers: [
        {provide: Router, useValue: routerSpy},
        {provide: ModalService, useValue: modalSpy},
        {provide: AuthService, useClass: AuthServiceMock},
        {provide: UserService, useClass: UserServiceMock},
        {provide: SignUpService, useClass: SignUpServiceMock},
      ],
    })
    .compileComponents();
  }));

  beforeEach((() => {
    fixture = TestBed.createComponent(SelectRepresentativeTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form invalid when empty', () => {
    expect(component.form.valid).toBeFalsy();
  });

  it('should route to attorney-welcome in case form submit', () => {
    component.form.patchValue(formDataMock);
    component.formSubmit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'attorney-welcome']
    );
  });

  it('should open pop up in case invalid form and attorney type', () => {
    const spy = spyOn(component, 'openRepTypeNotSelectedModal');

    component.formSubmit();
    expect(spy).toHaveBeenCalled();
  });

  it('should open pop up in case invalid form and acc rep type', () => {
    const spy = spyOn(component, 'openProfileNotLinkedModal');
    component.form.patchValue({representativeType: RepresentativeType.ACCREDITED_REPRESENTATIVE});

    component.formSubmit();
    expect(spy).toHaveBeenCalled();
  });

  it('representativeType field validity', () => {
    let errors = {};
    const representativeType = component.form.get('representativeType');
    expect(representativeType.valid).toBeFalsy();

    // representativeType is required
    errors = representativeType.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set representativeType to something correct
    representativeType.setValue('ATTORNEY');
    errors = representativeType.errors || {};
    expect(errors['required']).toBeFalsy();
  });
});
