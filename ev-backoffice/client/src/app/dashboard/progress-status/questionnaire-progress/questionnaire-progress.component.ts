import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';

import { ProgressStatus } from '../models/progress-status.model';
import { ProgressStatusService } from '../progress-status.service';


@Component({
  selector: 'app-questionnaire-progress',
  templateUrl: './questionnaire-progress.component.html',
  styleUrls: ['./questionnaire-progress.component.scss']
})

export class QuestionnaireProgressComponent implements OnInit {
  questionnaireProgress$: Observable<ProgressStatus[]>;

  constructor(
    private progressStatusService: ProgressStatusService,
  ) {
  }

  ngOnInit() {
    this.questionnaireProgress$ = this.progressStatusService.questionnaireProgress$;
  }
}
