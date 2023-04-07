import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NotificationsService, OrganizationService, UserService } from '../core/services';
import { UserServiceMock } from '../core/testing/user-service.mock';
import { TabsModule } from '../shared/components/tabs/tabs.module';
import { DashboardComponent } from './dashboard.component';
import { OrganizationServiceMock } from '../core/testing/organization-service.mock';
import { NotificationsServiceMock } from '../core/testing/notifications-service.mock';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [TabsModule, RouterTestingModule],
      declarations: [ DashboardComponent ],
      providers: [
        {provide: OrganizationService, useClass: OrganizationServiceMock },
        {provide: UserService, useClass: UserServiceMock },
        {provide: NotificationsService, useClass: NotificationsServiceMock },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
