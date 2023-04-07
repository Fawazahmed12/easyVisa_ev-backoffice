import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';


@Injectable({
  providedIn: 'root',
})
export class SuperAdminService {

  constructor(private httpClient: HttpClient) {

  }
  fetchQuestionnarieVersions() {
    return this.httpClient.get('admin/questionnaire-version');
  }

  fetchBenefitCategories( quest_version_id ) {
    return this.httpClient.get(`admin/questionnaire-version/${quest_version_id}`);
  }

  fetchForms( quest_version_id, benefit_Category_Id) {
    return this.httpClient.get(`admin/questionnaire-version/${quest_version_id}/benefit_category/${benefit_Category_Id}`);
  }

  fetchSections( quest_version_id, benefit_Category_Id,form_Id ) {
    return this.httpClient.get(`admin/questionnaire-version/${quest_version_id}/benefit_category/${benefit_Category_Id}/form/${form_Id}`);
  }

  fetchSubSections( quest_version_id,form_Id, section_Id ) {
    return this.httpClient.get(`admin/questionnaire-version/${quest_version_id}/form/${form_Id}/section/${section_Id}`);
  }

  fetchQuestions( quest_version_id, form_Id,subsection_Id ) {
    return this.httpClient.get(`admin/questionnaire-version/${quest_version_id}/form/${form_Id}/sub_section/${subsection_Id}`);
  }

  fetchmetadata(quest_version, quest_version_id ) {
    return this.httpClient.get(`admin/questionnaire-version/${quest_version}/question/${quest_version_id}`);
  }
}
