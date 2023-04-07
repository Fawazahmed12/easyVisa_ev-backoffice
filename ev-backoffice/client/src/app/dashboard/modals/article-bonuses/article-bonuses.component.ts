import { Component, OnInit, ViewChild } from '@angular/core';

import { OkButtonLg } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { ConfigDataService, ModalService } from '../../../core/services';
import { filter, pluck } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-article-bonuses',
  templateUrl: './article-bonuses.component.html',
})

export class ArticleBonusesComponent implements OnInit {
  @ViewChild('miniProfile', { static: true }) miniProfile;
  articleBonus$: Observable<number>;

  constructor(
    private modalService: ModalService,
    private configDataService: ConfigDataService,
  ) {
  }

  ngOnInit() {
    this.articleBonus$ = this.configDataService.feeDetails$.pipe(
      filter((feeDetails) => !!feeDetails),
      pluck('articleBonus'),
    );
  }

  openMiniProfileModal() {
    this.closeAllModals();
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.DASHBOARD.ARTICLES.MINI_PROFILE_MODAL.HEADER',
      body: this.miniProfile,
      buttons: [OkButtonLg],
      centered: true,
      size: 'lg',
    });
  }

  closeAllModals() {
    this.modalService.closeAllModals();
  }
}
