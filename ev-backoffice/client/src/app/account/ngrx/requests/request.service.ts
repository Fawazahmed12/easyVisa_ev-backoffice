import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';

import { Profile } from '../../../core/models/profile.model';

import { OrganizationProfile } from '../../profile/edit-preview-profile/models/organization-profile.model';
import { AttorneyProfile } from '../../profile/edit-preview-profile/models/attorney-profile.model';
import { EmployeeProfile } from '../../profile/edit-preview-profile/models/employee-profile.model';
import { NotificationTypes } from '../../models/notyfication-types.model';
import { Reminder } from '../../models/reminder.model';
import { Review } from '../../models/review.model';
import { Invite } from '../../models/invite.model';
import { map } from 'rxjs/operators';


@Injectable()
export class AccountModuleRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  profileGetRequest() {
    return this.httpClient.get<AttorneyProfile & EmployeeProfile & Profile>(`/profile`);
  }

  profilePutRequest(profile) {
    return this.httpClient.put<AttorneyProfile & EmployeeProfile & Profile>(`/profile`, profile);
  }

  profilePicturePostRequest(data) {
    return this.httpClient.post<{ url: string }>(`/users/${data.id}/profile-picture`, data.file);
  }

  profileEmailPutRequest(data) {
    return this.httpClient.put<{ email: string }>(`/profile/email`, { email: data });
  }

  organizationGetRequest(id) {
    return this.httpClient.get<OrganizationProfile>(`/organizations/${id}`);
  }

  organizationPutRequest(data) {
    return this.httpClient.put<OrganizationProfile>(`/organizations/${data.id}`, data);
  }

  organizationPicturePostRequest(data) {
    return this.httpClient.post<{ url: string }>(`/organizations/${data.id}/profile-picture`, data.file);
  }

  leaveOrganizationPostRequest(data) {
    return this.httpClient.post(`/organizations/${data.organizationId}/leave`, { employeeId: data.employeeId });
  }

  inviteAttorneyPutRequest(data) {
    return this.httpClient.put<Invite>(`legal-practice-invitee/ev-id/${data.easyVisaId}/email/${data.email}`, {});
  }

  inviteDeleteRequest() {
    return this.httpClient.delete<any>(`/legal-practice-invite`);
  }

  requestJoinPutRequest(data) {
    return this.httpClient.put(`/organizations/${data.easyVisaId}/join-request`, { email: data.email });
  }

  requestJoinDeleteRequest(data) {
    return this.httpClient.delete(`/organizations/${data.organizationId}/join-request/${data.requestId}`);
  }

  createReviewPostRequest(data) {
    return this.httpClient.post<Review>(`/review`, data);
  }

  reviewGetRequest(data) {
    return this.httpClient.get<Review>(`package/${data.packageId}/attorney/${data.representativeId}/review`);
  }

  reviewPutRequest(review) {
    return this.httpClient.put<Review>(`/review/${review.id}`, review);
  }

  reviewPatchRequest(review) {
    return this.httpClient.patch<Review>(`attorneys/review/${review.id}`, review);
  }

  reviewsGetRequest(params): Observable<{ body: Review[]; xTotalCount: string }> {
    return this.httpClient.get<Review[]>(`/attorneys/reviews`, {
        params,
        observe: 'response'
      },
    ).pipe(
      map(({ body, headers }) => {
        const xTotalCount = headers.get('x-total-count');
        return { body, xTotalCount };
      })
    );
  }

  ratingsGetRequest(id) {
    return this.httpClient.get(`/public/attorneys/${id}/ratings`);
  }

  isAdminPostRequest(data) {
    // return this.httpClient.post(`/attorneys/isadmin`, data);
    //  TODO return when back ready
    return of({ organizationId: 7 });
  }

  deleteNonRegisteredApplicant(applicantId) {
    return this.httpClient.delete<number>(`/applicants/${applicantId}`);
  }

  notificationsConfigGetRequest(id) {
    return this.httpClient.get<any>(`users/${id}/notifications`);
  }

  notificationsConfigPutRequest(data) {
    return this.httpClient.put<any>(`users/${data.id}/notifications`, {
      ...data.config,
      activeOrganizationId: data.activeOrganizationId
    });
  }

  remindersGetRequest(data) {
    return this.httpClient.get<Reminder[]>(`attorneys/${data}/notifications`);
  }

  remindersPatchRequest(data) {
    return this.httpClient.patch<Reminder[]>(`attorneys/${data.id}/notifications`, { attorneyNotifications: data.reminders,  activeOrganizationId: data.activeOrganizationId });
  }

  notificationTypesGetRequest() {
    return this.httpClient.get<NotificationTypes>(`attorneys/notifications/types`);
  }
}

