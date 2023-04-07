import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { FormlyFieldConfig } from '@ngx-formly/core';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import {
  Answer,
  AnswerModel,
  AnswerValidationModel,
  QuestionnaireAccessState,
  QuestionnaireModel,
  QuestionnaireParam,
  RepeatGroupModel,
  SectionWarningModel
} from '../../models/questionnaire.model';
import { FocusManagerService } from '../../services/focusmanager.service';


@Injectable()
export class QuestionnaireRequestService {

  requestOption = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'my-auth-token'
    })
  };

  constructor(private httpClient: HttpClient,
              private focusManagerService: FocusManagerService) {
  }

  questionnaireAccessGetRequest(packageId: string): Observable<QuestionnaireAccessState> {
    return this.httpClient.get<QuestionnaireAccessState>(`/questionnaire/access/package/${packageId}`);
  }

  sectionsGetRequest(packageId: string): Observable<QuestionnaireModel[]> {
    return this.httpClient.get<QuestionnaireModel[]>(`/package/${packageId}/sections`);
  }

  questionsGetRequest(questionnaireBody: QuestionnaireParam): Observable<FormlyFieldConfig[]> {
    const questionnaireUrl = `questionnaire/questions`;
    return this.httpClient.post<FormlyFieldConfig>(questionnaireUrl, questionnaireBody)
      .pipe(map(data => this.focusManagerService.addFieldFocusListener([ data ])));
  }

  answersGetRequest(questionnaireParam: QuestionnaireParam): Observable<Answer> {
    const packageId = questionnaireParam.packageId;
    const applicantId = questionnaireParam.applicantId;
    const sectionId = questionnaireParam.sectionId;
    const answerUrl = `questionnaire/packages/${packageId}/applicants/${applicantId}/sections/${sectionId}`;
    return this.httpClient.get<Answer>(answerUrl);
  }

  answerPostRequest(anserModel: AnswerModel): Observable<any> {
    return this.httpClient.post<any>(`answer`, anserModel)
      .pipe(map(data => this.formatSectionQuestionAnswerResponse(data))
      );
  }

  createRepeatingGroupInstance(repeatGroupModel: RepeatGroupModel): Observable<any> {
    return this.httpClient.post<any>(`repeatinggroup`, repeatGroupModel)
      .pipe(map(data => this.formatSectionQuestionAnswerResponse(data))
      );
  }

  removeRepeatingGroupInstance(repeatGroupModel: RepeatGroupModel): Observable<any> {
    return this.httpClient.post<any>(`repeatinggroup/remove`, repeatGroupModel)
      .pipe(map(data => this.formatSectionQuestionAnswerResponse(data))
      );
  }

  formatSectionQuestionAnswerResponse(data) {
    return {
      ...data,
      sectionQuestions: this.focusManagerService.addFieldFocusListener([ data.sectionQuestions ])
    };
  }

  questionnaireCompletionWarningRequest(questionnaireParam: QuestionnaireParam): Observable<SectionWarningModel> {
    const questionnaireCompletionWarningUrl = `questionnaire/completionwarning`;
    return this.httpClient.post<SectionWarningModel>(questionnaireCompletionWarningUrl, questionnaireParam);
  }

  answerValidationRequest(answerModel: AnswerModel): Observable<AnswerValidationModel> {
    return this.httpClient.post<AnswerValidationModel>(`answer/validate`, answerModel);
  }
}
