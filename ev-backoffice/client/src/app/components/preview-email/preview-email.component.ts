import { Component, Input, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, publishReplay, refCount } from 'rxjs/operators';

import { EmailsService } from '../../core/services';
import { PreviewedEmail } from '../../core/models/previewed-email.model';
import { RequestState } from '../../core/ngrx/utils';
import { Email } from '../../core/models/email.model';


@Component({
  selector: 'app-preview-email',
  templateUrl: './preview-email.component.html',
})
export class PreviewEmailComponent implements OnInit {
  @Input() emailData: Email = null;

  previewedEmail$: Observable<RequestState<PreviewedEmail>>;

  constructor(
    private emailsService: EmailsService,
  ) {

  }

  ngOnInit() {
    this.previewedEmail$ = this.emailsService.previewUnsavedEmail(this.emailData).pipe(
      filter(response => !response.loading),
      publishReplay(1),
      refCount(),
    );
  }
}
