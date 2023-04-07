import { Component, Input, OnInit } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';

import { InviteRequestService } from '../../services/invite-request.service';
import { Invite } from '../../models/invite.model';


@Component({
  selector: 'app-invitation-request-sent',
  templateUrl: './invitation-request-sent.component.html',
})

export class InvitationRequestSentComponent implements OnInit {
  private _invite: Observable<Invite>;
  @Input() isRequest = true;
  inviteDefault$: Observable<Invite>;

  @Input()
  set invite(value) {
    this._invite = value;
  }
  get invite() {
    return this._invite || this.inviteDefault$;
  }

  constructor(
    private activeModal: NgbActiveModal,
    private inviteRequestService: InviteRequestService,
  ) {
  }

  ngOnInit() {
    this.inviteDefault$ = this.inviteRequestService.invite$;
  }

  closeModal() {
    this.activeModal.close();
  }
}
