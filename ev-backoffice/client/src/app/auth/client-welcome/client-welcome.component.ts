import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-client-welcome',
  templateUrl: './client-welcome.component.html',
  styleUrls: ['./client-welcome.component.scss'],
})
export class ClientWelcomeComponent {

  constructor(
    private router: Router,
  ) {
  }

  goToProfile() {
    this.router.navigate([ 'dashboard', 'progress-status' ]);
  }
}
