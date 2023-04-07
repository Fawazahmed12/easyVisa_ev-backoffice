import { Component, OnInit } from '@angular/core';

import { combineLatest, Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';

import { User } from '../../core/models/user.model';
import { Role } from '../../core/models/role.enum';
import { AuthService, NotificationsService, OrganizationService, PackagesService, UserService } from '../../core/services';
import { rolesHasAccess } from '../../shared/utils/roles-has-access';
import { Attorney } from '../../core/models/attorney.model';
import { Package } from '../../core/models/package/package.model';
import { PackageStatus } from '../../core/models/package/package-status.enum';
import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';

@Component({
  selector: 'app-navigation',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent implements OnInit {

  navbarMenu$: Observable<{
    title: string;
    link: string[];
    display: boolean;
    cssClasses?: {};
    taskQueueCount?: boolean;
    showExclamation?: boolean;
  }[]>;
  taskQueueCount$: Observable<number>;
  currentUser$: Observable<User>;
  isLoggedIn$: Observable<boolean>;
  hasCloseDropdown = false;


  constructor(
    private userService: UserService,
    private notificationsService: NotificationsService,
    private packagesService: PackagesService,
    private organizationService: OrganizationService,
    private authService: AuthService,
  ) {
  }

  ngOnInit() {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
    this.taskQueueCount$ = this.notificationsService.taskQueueCount$;
    this.currentUser$ = this.userService.currentUser$;
    this.navbarMenu$ = this.currentUser$.pipe(
      filter((user: User) => !!user),
      switchMap((user) => combineLatest([
          this.organizationService.withoutOrganizations$,
          this.packagesService.activePackageId$,
          this.packagesService.activePackage$,
          this.organizationService.currentPosition$,
          this.organizationService.isAdmin$,
        ]).pipe(
          map(([withoutOrganizations, packageId, activePackage, currentPosition, isAdmin]) => [user, withoutOrganizations, packageId, activePackage, currentPosition, isAdmin])
        )
      ),
      map(([user, withoutOrganizations, packageId, activePackage, currentPosition, isAdmin]: [User, boolean, number, Package, EmployeePosition, boolean]) => {
        const isUser = user.roles.some((role) => role === Role.ROLE_USER);
        const isEv = user.roles.includes(Role.ROLE_OWNER);
        const isTrainee = currentPosition === EmployeePosition.TRAINEE;
        const isEmployee = currentPosition === EmployeePosition.EMPLOYEE;
        const isManagerOnly = currentPosition === EmployeePosition.MANAGER && !isAdmin
        return [
          {
            title: 'TEMPLATE.HEADER.NAV.TYPES_OF_IMMIGRATION',
            link: ['/types-of-immigration'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.FIND_AN_ATTORNEY',
            link: ['/find-an-attorney'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.VISAS',
            link: ['/visas'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.GREEN_CARD',
            link: ['/green-card'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.CITIZENSHIP',
            link: ['/citizenship'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.ADMISSIBILITY_ISSUES',
            link: ['/admissibility-issues'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.FIANCE_VS_MARRIAGE_VISA',
            link: ['/fiancé-vs-marriage-visa'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.VISA_PROCESS',
            link: ['/visa-process'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.NEWS',
            link: ['/news'],
            display: false,
          },
          {
            title: 'TEMPLATE.HEADER.NAV.DASHBOARD',
            link: ['/dashboard'],
            display: !!user && !withoutOrganizations || isUser,
            menu: [
              {
                title: 'TEMPLATE.HEADER.NAV.FINANCIAL',
                link: ['/financial'],
                display: !!user && !isUser,
                cssClasses: {
                  disable: isTrainee || isEmployee || isManagerOnly
                }
              },
              {
                title: 'TEMPLATE.HEADER.NAV.MARKETING',
                link: ['/marketing'],
                display: !!user && !isUser,
                cssClasses: {
                  disable: isTrainee || isEmployee || isManagerOnly
                }
              },
              {
                title: 'TEMPLATE.HEADER.NAV.ARTICLES',
                link: ['/articles'],
                display: !!user && !isUser,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.PROCESS_LINKS',
                link: ['/process-links'],
                display: !!user && !isUser,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.PROGRESS_STATUS',
                link: ['/progress-status'],
                display: isUser,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.TUTORIALS',
                link: ['/tutorials'],
                display: !!user,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.SETTINGS',
                link: ['/settings'],
                display: isEv,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.USCIS_DATES',
                link: ['/uscis-edition-dates'],
                display: !!user && !isUser,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.ALERTS',
                link: ['/alerts'],
                display: isUser,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.PROCESS_LINKS',
                link: ['/process-links'],
                display: isUser,
              },
            ]
          },
          {
            title: 'TEMPLATE.HEADER.NAV.TASK_QUEUE',
            link: ['/task-queue'],
            taskQueueCount: true,
            display: !!user && rolesHasAccess(user.roles, [Role.ROLE_OWNER, Role.ROLE_ATTORNEY, Role.ROLE_EMPLOYEE, Role.ROLE_EV]) && !withoutOrganizations,
            menu: [
              {
                title: 'TEMPLATE.HEADER.NAV.DISPOSITIONS',
                link: ['/dispositions'],
                display: !!user && !withoutOrganizations,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.ALERTS',
                link: ['/alerts'],
                display: !!user && !withoutOrganizations,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.WARNINGS',
                link: ['/warnings'],
                display: !!user && !withoutOrganizations,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.CLIENTS',
                link: ['/clients'],
                display: !!user && !withoutOrganizations,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.CREATE_EDIT_PACKAGE',
                link: ['/package'],
                display: !!user && !withoutOrganizations,
                cssClasses: {
                  disable: isTrainee || isEmployee
                }
              },
              {
                title: 'TEMPLATE.HEADER.NAV.ADDITIONAL_FEES',
                link: ['/additional-fees'],
                display: !!user && !withoutOrganizations,
                cssClasses: {
                  disable: activePackage?.status == PackageStatus.TRANSFERRED || isTrainee || isEmployee
                }
              },
            ]
          },
          {
            title: 'TEMPLATE.HEADER.NAV.QUESTIONNAIRE',
            link: ['/questionnaire', 'package', packageId ? `${packageId}` : ''],
            display: !!user && !withoutOrganizations || isUser,
            cssClasses: {
              dimmed: !this.canDisplayMenuTab(user, packageId)
            },
          },
          {
            title: 'TEMPLATE.HEADER.NAV.DOCUMENTS',
            link: ['/documents', 'package', packageId ? `${packageId}` : ''],
            display: !!user && !withoutOrganizations || isUser,
            cssClasses: {
              dimmed: !this.canDisplayMenuTab(user, packageId)
            },
          },
          {
            title: 'TEMPLATE.HEADER.NAV.MY_ACCOUNT',
            link: ['/account'],
            display: !!user && !withoutOrganizations || isUser,
            showExclamation: user && -(user.profile as Attorney).balance < 0,
            menu: [
              {
                title: 'TEMPLATE.HEADER.NAV.PROFILE',
                link: ['/profile'],
                display: !!user && !withoutOrganizations || isUser,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.PAYMENT_FEE_SCHEDULE',
                link: ['/payment-fee-schedule'],
                display: !!user && !isUser && !withoutOrganizations,
                cssClasses: {
                  disable: isTrainee || isEmployee
                }

              },
              {
                title: 'TEMPLATE.HEADER.NAV.NOTIFICATIONS_REMINDERS',
                link: ['/notifications-reminders'],
                display: !!user && !isUser && !withoutOrganizations,
                cssClasses: {
                  disable: isTrainee || isEmployee
                }
              },
              {
                title: 'TEMPLATE.HEADER.NAV.EMAIL_TEMPLATES',
                link: ['/email-templates'],
                display: !!user && !isUser && !withoutOrganizations,
                cssClasses: {
                  disable: isTrainee || isEmployee
                }
              },
              {
                title: 'TEMPLATE.HEADER.NAV.PERMISSIONS',
                link: ['/permissions'],
                display: !!user && !isUser && !withoutOrganizations,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.WRITE_REVIEW',
                link: ['/write-a-review'],
                display: !!user && isUser,
              },
              {
                title: 'TEMPLATE.HEADER.NAV.LEGAL_REPRESENTATIVE',
                link: ['/legal-representative'],
                display: !!user && isUser,
              },
            ]
          },
          {
            title: 'TEMPLATE.HEADER.NAV.SUPER_ADMIN',
            link: ['/super-admin'],
            display: isEv,
          }
        ];
      })
    );
  }

  canDisplayMenuTab(user, packageId) {
    const hasValidRole = rolesHasAccess(user.roles, [Role.ROLE_ATTORNEY, Role.ROLE_EMPLOYEE, Role.ROLE_USER]);
    return hasValidRole && packageId;
  }

  closeDropdownMenu($event) {
    this.hasCloseDropdown = true;
  }

  openDropdownMenu($event) {
    this.hasCloseDropdown = false;
  }
}
