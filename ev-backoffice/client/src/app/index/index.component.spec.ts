/* eslint-disable no-unused-vars, @typescript-eslint/no-unused-vars */

import { TestBed, waitForAsync } from '@angular/core/testing';
import { Routes } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';

import { UserService } from '../core/services';
import { UserServiceMock } from '../core/testing';
import { SpinnerModule } from '../components/spinner/spinner.module';

import { IndexComponent } from './index.component';

describe('Component: Index', () => {

  const rootRouterConfig: Routes = [
    {path: '', redirectTo: 'index', pathMatch: 'full'},
    {path: 'index', component: IndexComponent},
  ];

  let component: IndexComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        IndexComponent
      ],
      imports: [
        RouterTestingModule.withRoutes(rootRouterConfig),
        SpinnerModule,
      ],
      providers: [
        {provide: APP_BASE_HREF, useValue: '/'},
        {provide: UserService, useClass: UserServiceMock},
      ],
    });

    const fixture = TestBed.createComponent(IndexComponent);
    component = fixture.debugElement.componentInstance;
  });

  it('should create the component', waitForAsync(() => {
    expect(component).toBeTruthy();
  }));

});
