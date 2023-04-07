import { Component, Input } from '@angular/core';

import { Profile } from '../../../../../../../../core/models/profile.model';

@Component({
  selector: 'app-good-news-modal',
  templateUrl: './good-news-modal.component.html',
})

export class GoodNewsModalComponent {
  @Input() findApplicant: {profile: Profile; inOpenPackage: boolean; inBlockedPackage: boolean};
}
