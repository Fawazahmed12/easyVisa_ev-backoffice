import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ReferringUserModel } from '../models/referring-user.model';
import { FeeDetails } from '../../core/models/fee-details.model';
import { ConfigDataService } from '../../core/services';

@Component({
  selector: 'app-attorney-landing-page',
  templateUrl: './attorney-landing-page.component.html',
  styleUrls: [ './attorney-landing-page.component.scss' ],
})
export class AttorneyLandingPageComponent implements OnInit {

  referringUser$: Observable<ReferringUserModel>;
  feeDetails$: Observable<FeeDetails> = this.configDataService.feeDetails$;

  cards = [
    {
      img: '../.././../assets/images/online_questionnaire.png',
      title: 'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.SUB_TITLE_QUESTIONNAIRE',
      items: [
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.QUESTIONNAIRE_POINT_1',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.QUESTIONNAIRE_POINT_2',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.QUESTIONNAIRE_POINT_3',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.QUESTIONNAIRE_POINT_4',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.QUESTIONNAIRE_POINT_5'
      ],
    },
    {
      img: '../.././../assets/images/image_doc_portal.png',
      title: 'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.SUB_TITLE_DOC_PORTAL',
      items: [
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.DOC_PORTAL_POINT_1',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.DOC_PORTAL_POINT_2',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.DOC_PORTAL_POINT_3',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.DOC_PORTAL_POINT_4'
      ],
    },
    {
      img: '../.././../assets/images/image_progress.png',
      title: 'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.SUB_TITLE_PROGRESS_TRACKING',
      items: [
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.PROGRESS_TRACKING_POINT_1',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.PROGRESS_TRACKING_POINT_2',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.PROGRESS_TRACKING_POINT_3',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.PROGRESS_TRACKING_POINT_4',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.PROGRESS_TRACKING_POINT_5'
      ],
    },
  ];

  forYouCards = [{
    img: '../.././../assets/images/image_progress.png',
    title: 'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.TITLE_FOR_YOU',
    items: [
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_1',
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_2',
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_3',
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_4',
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_5',
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_6',
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_7',
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_8',
      'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.EASYVISA_CAN_DO_FOR_YOU.FOR_YOU_POINT_9',
    ],
  }];

  testimonials = [
    {
      text: [
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.TESTIMONIAL_1_1',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.TESTIMONIAL_1_2',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.TESTIMONIAL_1_3',
      ],
    },
    {
      text: [
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.TESTIMONIAL_2_1',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.TESTIMONIAL_2_2',
      ],
    },
    {
      text: [
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.TESTIMONIAL_3_1',
        'TEMPLATE.AUTH.ATTORNEY_LANDING_PAGE.TESTIMONIAL_3_2',
      ],
    },
  ];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private configDataService: ConfigDataService,
  ) {

  }

  ngOnInit() {
    this.referringUser$ = this.activatedRoute.data.pipe(
      filter((data) => !!data.referringUser && !data.referringUser.message),
      map((data) => data.referringUser),
    );
  }

  signUpClick() {
    this.router.navigate(
      [ 'auth', 'attorney-sign-up' ],
      {
        queryParamsHandling: 'merge',
      }
    );
  }

}
