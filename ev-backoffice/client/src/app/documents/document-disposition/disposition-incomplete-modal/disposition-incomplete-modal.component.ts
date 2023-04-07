import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';

@Component({
  selector: 'app-disposition-incomplete-modal',
  templateUrl: './disposition-incomplete-modal.component.html',
  styleUrls: ['./disposition-incomplete-modal.component.scss']
})
export class DispositionIncompleteModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal, private router: Router) { }

  ngOnInit() {
  }

  gotoDispositionMenu() {
    this.activeModal.dismiss('Cross click');
    this.router.navigate(['task-queue', 'dispositions']);
  }
}
