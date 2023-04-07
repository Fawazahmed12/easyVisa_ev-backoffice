import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { switchMap, tap } from 'rxjs/operators';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { PdfPrintTestingService } from './pdf-print-testing.service';
import { ContinuationSheetModel, USCISFormModel } from './pdf-print-testing.model';
import { PdfViewerModalComponent } from './pdf-viewer-modal/pdf-viewer-modal.component';


@Component({
  selector: 'app-pdf-print-testing',
  templateUrl: './pdf-print-testing.component.html',
  styleUrls: [ './pdf-print-testing.component.scss' ]
})
export class PdfPrintTestingComponent implements OnInit {

  @Input() packageId;

  applicantId: number;
  uscisForms: USCISFormModel[] = [];
  continuationSheets: ContinuationSheetModel[] = [];
  selectedUSCISFormId: String;
  selectedContinuationSheetd: String;
  isPdfGenerateProcessing = false;

  constructor(private pdfPrintService: PdfPrintTestingService,
              private activatedRoute: ActivatedRoute, private ngbModal: NgbModal) {
  }

  ngOnInit() {
    this.activatedRoute.params.pipe(
      tap((params) => this.applicantId = params.applicantId),
      switchMap((params) => this.pdfPrintService.fetchQuestionnaireForms(this.packageId))
    ).subscribe((uscisForms) => {
      this.uscisForms = uscisForms;
      this.resetFormList();
    });
  }


  onUSCISFormChange(selectedUSCISForm) {
    selectedUSCISForm.continuationSheetList.forEach((continuationSheet) => {
      continuationSheet[ 'displayName' ] = `[Item ${continuationSheet.item}] - ${continuationSheet.displayText}`;
    });
    this.continuationSheets = selectedUSCISForm.continuationSheetList || [];
    this.selectedContinuationSheetd = null;
  }


  onContinuationSheetChange(selectedContinuationSheet) {
    console.log('Selected ContinuationSheet Id: ' + selectedContinuationSheet.id);
  }


  onPdfPrint() {
    const pdfPrintData = {
      packageId: this.packageId,
      applicantId: +this.applicantId
    };

    if (this.selectedContinuationSheetd != null) {
      pdfPrintData[ 'continuationSheetId' ] = this.selectedContinuationSheetd;
    } else {
      pdfPrintData[ 'formId' ] = this.selectedUSCISFormId;
    }

    this.isPdfGenerateProcessing = true;
    this.pdfPrintService.printPdfForm(pdfPrintData).subscribe((data) => {
      const file = new Blob([ data ], { type: 'application/pdf' });
      const fileURL = URL.createObjectURL(file);
      const modalRef = this.ngbModal.open(PdfViewerModalComponent, {
        size: 'lg',
        backdrop: 'static',
        windowClass: 'pdf-viewer-modal'
      });
      modalRef.componentInstance.pdfFileUrl = fileURL;
      this.isPdfGenerateProcessing = false;
      this.resetFormList();
    }, (error) => {
      this.isPdfGenerateProcessing = false;
      console.log('Pdf Print Error' + error);
    });
  }

  resetFormList() {
    this.selectedContinuationSheetd = null;
    this.selectedUSCISFormId = null;
    this.continuationSheets = [];
  }

}
