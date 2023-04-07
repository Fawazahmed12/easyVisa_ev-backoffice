import { Component } from '@angular/core';

import { VideoConfig } from '../models/video-config.model';


@Component({
  selector: 'app-tutorials',
  templateUrl: './tutorials.component.html',
})

export class TutorialsComponent {

  taskQueueVideos: VideoConfig[] = [
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.TASK_QUEUE',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.ALERTS',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.WARNINGS',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.CLIENT_SEARCH',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.CREATE_EDIT_MESSAGE',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.ADDITIONAL_FEES',
      source: '',
      poster: ''
    },
  ];

  myAccountVideos: VideoConfig[] = [
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.PROFILE',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.PAYMENT_FEE_SCHEDULE',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.NOTIFICATIONS_REMINDERS',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.EMAIL_TEMPLATES',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.PERMISSIONS',
      source: '',
      poster: ''
    },
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.LOG',
      source: '',
      poster: ''
    },
  {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.ADMIN',
      source: '',
      poster: ''
    },
  ];

  questionnaireVideos: VideoConfig[] = [
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.QUESTIONNAIRE',
      source: '',
      poster: ''
    }
  ];

  documentsVideos: VideoConfig[] = [
    {
      title: 'TEMPLATE.DASHBOARD.FINANCIAL.TUTORIALS.DOCUMENTS',
      source: '',
      poster: ''
    }
  ];
}
