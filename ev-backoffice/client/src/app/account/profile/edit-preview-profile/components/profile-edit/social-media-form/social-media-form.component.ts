import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-social-media-form',
  templateUrl: './social-media-form.component.html',
  styleUrls: ['./social-media-form.component.scss'],
})

export class SocialMediaFormComponent {
  @Input() profileFormGroup: FormGroup;

  get facebookUrlFormControl() {
    return this.profileFormGroup.get('facebookUrl');
  }

  get linkedinUrlFormControl() {
    return this.profileFormGroup.get('linkedinUrl');
  }

  get youtubeUrlFormControl() {
    return this.profileFormGroup.get('youtubeUrl');
  }

  get twitterUrlFormControl() {
    return this.profileFormGroup.get('twitterUrl');
  }
}
