import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ProgressStatus } from '../models/progress-status.model';
import { ProgressStatusService } from '../progress-status.service';

@Component({
  selector: 'app-document-portal-progress',
  templateUrl: './document-portal-progress.component.html',
  styleUrls: ['./document-portal-progress.component.scss']
})
export class DocumentPortalProgressComponent implements OnInit {
  documentProgress$: Observable<ProgressStatus[]>;

  constructor(
    private progressStatusService: ProgressStatusService,
  ) {
  }

  ngOnInit() {
    this.documentProgress$ = this.progressStatusService.documentProgress$;
  }
}
