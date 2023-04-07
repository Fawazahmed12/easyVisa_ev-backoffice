import { Injectable } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, CanDeactivate, Router,
  RouterStateSnapshot, UrlTree, UrlSegmentGroup, UrlSegment } from '@angular/router';
import { Observable, of, combineLatest } from 'rxjs';
import { catchError, switchMap, take, tap } from 'rxjs/operators';

import { QuestionnaireService } from '../services';
import { SectionComponent } from '../formly/section/section.component';
import { ModalService, UserService } from '../../core/services';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';

const PRIMARY_OUTLET = 'primary';
const DEFAULT_PARENT_SEGMENTS_LENGTH = 3;

@Injectable()
export class DeactiveQuestionnaireGuardService implements CanDeactivate<SectionComponent> {

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private questionnaireService: QuestionnaireService,
    private modalService: ModalService,
    private userService: UserService,
  ) {
  }


  canDeactivate(component: SectionComponent, currentRoute: ActivatedRouteSnapshot,
                currentState: RouterStateSnapshot, nextState: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    if(this.hasSameQuestionnaireScreenNavigation(currentState, nextState)){
      return of(false);
    }

    const previousNavigationUrlState = this.questionnaireService.getRecentNavigationState();
    if (this.hasRecentNavigationState(previousNavigationUrlState, currentState, nextState)) {
      this.questionnaireService.setRecentNavigationState({});
      return of(previousNavigationUrlState.canProceedNavigation);
    }

    return combineLatest([this.questionnaireService.hasPendingAnswerSaveRequest$, this.userService.currentUser$])
      .pipe(switchMap(([ hasPendingAnswerSave, user ]) =>
        // check if user exists or not... If not, then create default'sectionWarningData' object...
         (user && !hasPendingAnswerSave) ? component.getSectionWarningData() : of({ hasSectionWarning: false, hasPendingAnswerSave })
      ),
      switchMap((data: any) => data.hasSectionWarning ? this.openSectionWarningModal(data, currentState, nextState) : of(!data.hasPendingAnswerSave)),
      tap((data) => {
        const currentNavigationUrlState = {
          currentState: currentState.url,
          nextState: nextState.url,
          canProceedNavigation: data
        };
        this.questionnaireService.setRecentNavigationState(currentNavigationUrlState);
      }),
      take(1),
      catchError((err) => of(true))
    );
  }


  private openSectionWarningModal(sectionWarningModel, currentState: RouterStateSnapshot, nextState: RouterStateSnapshot) {
    return this.modalService.openConfirmModal({
      header: sectionWarningModel.headerText,
      body: sectionWarningModel.warningMessage,
      buttons: [
        {
          label: sectionWarningModel.leftButtonText,
          type: ConfirmButtonType.Close,
          className: 'btn btn-primary mr-2 min-w-100',
          value: false // This value don't allow the routing
        },
        {
          label: sectionWarningModel.rightButtonText,
          type: ConfirmButtonType.Close,
          className: 'btn btn-primary mr-2 min-w-100',
          value: true  // This value allows the routing
        },
      ],
      centered: true,
      backdrop: 'static',
      showCloseIcon: false,
      size: 'lg',
    });
  }

  private hasRecentNavigationState(previousNavigationUrlState: any, currentState: any, nextState: any): boolean {
    return previousNavigationUrlState.currentState === currentState.url &&
      nextState.url.indexOf(previousNavigationUrlState.nextState) !== -1;
  }

  private hasSameQuestionnaireScreenNavigation(currentState: RouterStateSnapshot, nextState: RouterStateSnapshot){
    const tree: UrlTree = this.router.parseUrl(nextState.url);
    const segmentGroup: UrlSegmentGroup = tree.root.children[PRIMARY_OUTLET];
    const segments: UrlSegment[] = segmentGroup.segments;
    return segments.length == DEFAULT_PARENT_SEGMENTS_LENGTH && currentState.url.indexOf(nextState.url) !== -1;
  }
}
