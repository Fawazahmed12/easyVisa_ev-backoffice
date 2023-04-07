import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-document-help',
  templateUrl: './document-help.component.html',
  styleUrls: ['./document-help.component.scss']
})
export class DocumentHelpComponent implements OnInit {

  @Input() documentHelpText;

  constructor() { }

  ngOnInit() {
  }

}
