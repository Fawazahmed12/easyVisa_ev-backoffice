import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BehaviorSubject, merge, Observable, OperatorFunction, Subject } from 'rxjs';
import { SuperAdminService } from './super-admin.service';
import { ActivatedRoute, Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, filter, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { AdminRouteParams } from './super-admin.type';
@Component({
  selector: 'app-super-admin',
  templateUrl: './super-admin.component.html',
  styleUrls: ['./super-admin.component.scss']
})




export class SuperAdminComponent implements OnInit {
  searchText: string;
  SuperAdminCreationForm: FormGroup;
  benefitCategory$ = new BehaviorSubject<any>([]);
  questionnaireVersion$ = new BehaviorSubject<any>([]);
  form$ = new BehaviorSubject<any>([]);
  section$ = new BehaviorSubject<any>([]);
  subSection$ = new BehaviorSubject<any>([]);
  question$ = new BehaviorSubject<any>([]);
  updateQuestionByFilterText = new Subject<boolean>();
  metadata$ = new BehaviorSubject<any>([]);
  get version() {
    return this.SuperAdminCreationForm.get('version') as FormArray;
  }

  get benefitCategory() {
    return this.SuperAdminCreationForm.get('benefitCategory') as FormArray;
  }

  get form() {
    return this.SuperAdminCreationForm.get('form') as FormArray;
  }

  get section() {
    return this.SuperAdminCreationForm.get('section') as FormArray;
  }

  get subSection() {
    return this.SuperAdminCreationForm.get('subSection') as FormArray;
  }

  get question() {
    return this.SuperAdminCreationForm.get('question') as FormArray;
  }

  constructor(
    private formBuilder: FormBuilder,
    private superAdminService: SuperAdminService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    this.SuperAdminCreationForm = this.formBuilder.group({
      version: ['', [Validators.required]],
      benefitCategory: ['', [Validators.required]],
      form: ['', [Validators.required]],
      section: ['', [Validators.required]],
      subSection: ['', [Validators.required]],
      question: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    this.superAdminService.fetchQuestionnarieVersions().subscribe(data => {
      this.questionnaireVersion$.next(data);
    });
    this.version?.valueChanges.subscribe(quest_version_id => {
      const queryParams = { ...this.activatedRoute.snapshot.queryParams, quest_version_id };
      this.updateQueryParams(queryParams);
    });

    this.benefitCategory?.valueChanges.subscribe(benefit_category_id => {
      const queryParams = { ...this.activatedRoute.snapshot.queryParams, benefit_category_id };
      this.updateQueryParams(queryParams);
    });

    this.form?.valueChanges.subscribe(form_id => {
      const queryParams = { ...this.activatedRoute.snapshot.queryParams, form_id };
      this.updateQueryParams(queryParams);
    });

    this.section?.valueChanges.subscribe(section_id => {
      const queryParams = { ...this.activatedRoute.snapshot.queryParams,section_id };
      this.updateQueryParams(queryParams);
    });

    this.subSection?.valueChanges.subscribe(subsection_id => {
      const queryParams = { ...this.activatedRoute.snapshot.queryParams,subsection_id };
      this.updateQueryParams(queryParams);
    });

    this.activatedRoute.queryParams.pipe(
      map((params: AdminRouteParams) => params.quest_version_id),
      filter(quest_version_id => !!quest_version_id),
      distinctUntilChanged(),
      switchMap(quest_version_id => {
        this.SuperAdminCreationForm.patchValue({
          version: quest_version_id
        });
        return this.superAdminService.fetchBenefitCategories(quest_version_id);
      })).subscribe((data) => {
        this.benefitCategory$.next(data);
      }
    );
    this.activatedRoute.queryParams.pipe(
      filter(({ benefit_category_id, quest_version_id }: AdminRouteParams) => !!benefit_category_id && !!quest_version_id),
      distinctUntilChanged((prev,curr) => prev.benefit_category_id === curr.benefit_category_id ),
      switchMap(({ benefit_category_id, quest_version_id }) => {
        this.SuperAdminCreationForm.patchValue({
          benefitCategory: benefit_category_id
        });
        return this.superAdminService.fetchForms(quest_version_id, benefit_category_id);
      })
    ).subscribe((data) => {
        this.form$.next(data);
      }
    );
    this.activatedRoute.queryParams.pipe(
      filter(({ quest_version_id, benefit_category_id, form_id }: AdminRouteParams) => !!form_id && !!quest_version_id && !!benefit_category_id),
      distinctUntilChanged((prev,curr) => prev.form_id === curr.form_id ),
      switchMap(({ quest_version_id, benefit_category_id, form_id }) => {
        this.SuperAdminCreationForm.patchValue({
          form: form_id
        });
        return this.superAdminService.fetchSections(quest_version_id, benefit_category_id, form_id);
      })
    ).subscribe((data) => {
        this.section$.next(data);
      }
    );
    this.activatedRoute.queryParams.pipe(
      filter(({quest_version_id,form_id, section_id}: AdminRouteParams) => !!section_id && !!form_id && !!quest_version_id),
      distinctUntilChanged((prev,curr) => prev.section_id === curr.section_id ),
      switchMap(({quest_version_id,form_id, section_id}) => {
        this.SuperAdminCreationForm.patchValue({
          section:section_id
        });
        return this.superAdminService.fetchSubSections(quest_version_id,form_id, section_id);
      })
    ).subscribe((data) => {
        this.subSection$.next(data);
      }
    );
    this.activatedRoute.queryParams.pipe(
      tap(data => console.log(data)),
      filter(({quest_version_id,subsection_id}: AdminRouteParams) => !!subsection_id && !!quest_version_id),
      tap(data => console.log(data)),
      distinctUntilChanged((prev,curr) => prev.subsection_id === curr.subsection_id ),
      tap(data => console.log(data)),
      switchMap(({quest_version_id,subsection_id,form_id}) => {
        this.SuperAdminCreationForm.patchValue({
          subSection:subsection_id
        });
        return this.superAdminService.fetchQuestions(quest_version_id,form_id,subsection_id);
      })
    ).subscribe((data) => {
        this.question$.next(data);
      }
    );

    // this.updateQuestionByFilterText.pipe(
    //   withLatestFrom(this.question$),
    //   tap(([_,question]) => console.log(question)),
    //   map(([_,question]) => {
    //     return  (this.searchText ? question.filter(s => s.id.toLowerCase().includes(this.searchText.toLowerCase())) : question);
    //     }
    //   )
    // ).subscribe((question) => {
    //   console.log(this.searchText);
    //   console.log(question);
    //   this.filteredQuestion$.next(question);
    // }
    // );

    this.activatedRoute.queryParams.pipe(
      filter(({quest_version_id,question_id}: AdminRouteParams) =>  !!quest_version_id && !!question_id),
      distinctUntilChanged((prev,curr) => prev.question_id === curr.question_id ),
      switchMap(({quest_version_id,question_id}) => {
        this.searchText = question_id;
        return this.superAdminService.fetchmetadata(quest_version_id,question_id);
      })
    ).subscribe((data) => {
        this.metadata$.next(data);
      }
    );
  }

  updateQueryParams(queryParams) {
    this.router?.navigate(['super-admin'], { queryParams });
  }
  onSearch() {
    this.updateQuestionByFilterText.next(true);
  }

  getMetadata() {
    const queryParams = { ...this.activatedRoute.snapshot.queryParams,question_id:this.searchText };
    this.updateQueryParams(queryParams);
  }
}
