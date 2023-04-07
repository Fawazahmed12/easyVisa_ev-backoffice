import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DocumentsService } from '../../services';
import { Subject } from 'rxjs';
import { DestroySubscribers } from 'ngx-destroy-subscribers';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-disposition-complete-modal',
  templateUrl: './disposition-complete-modal.component.html',
  styleUrls: [ './disposition-complete-modal.component.scss' ]
})
@DestroySubscribers()
export class DispositionCompleteModalComponent implements OnInit, OnDestroy {

  @Input() document;
  @Input() inputData;

  submitted = false;
  dispositionApproveDocument$: Subject<any> = new Subject<any>();
  private subscribers: any = {};

  constructor(public activeModal: NgbActiveModal, private documentsService: DocumentsService) {
  }

  ngOnInit() {
    this.subscribers.documentApprovalSubscription = this.dispositionApproveDocument$.pipe(
      switchMap((data) => this.documentsService.approvedEntireDocumentPanel(data)))
      .subscribe((data) => {
        this.activeModal.dismiss('Cross click');
      }, (error) => {
        this.activeModal.dismiss('Cross click');
      });
  }

  onApproveEntirePanel() {
    this.submitted = true;
    const inputData = {
      packageId: this.inputData.packageId,
      applicantId: this.inputData.applicantId,
      attachmentRefId: this.inputData.attachmentRefId,
      documentType: this.inputData.documentType,
      isApproved: this.inputData.isApproved
    };
    this.dispositionApproveDocument$.next(inputData);
  }

  ngOnDestroy() {
    if (this.subscribers.documentApprovalSubscription) {
      this.subscribers.documentApprovalSubscription.unsubscribe();
      this.subscribers.documentApprovalSubscription = null;
    }
  }
}
