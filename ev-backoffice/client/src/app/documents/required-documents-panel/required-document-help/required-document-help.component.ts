import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-required-document-help',
  templateUrl: './required-document-help.component.html',
  styleUrls: ['./required-document-help.component.scss']
})
export class RequiredDocumentHelpComponent implements OnInit {

  @Input() requiredDocument;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

}
