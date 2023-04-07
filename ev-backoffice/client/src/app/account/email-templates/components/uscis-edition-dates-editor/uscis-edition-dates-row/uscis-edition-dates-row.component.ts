import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-uscis-edition-dates-row',
  templateUrl: './uscis-edition-dates-row.component.html',
  styleUrls: [ './uscis-edition-dates-row.component.scss' ]
})
export class UscisEditionDatesRowComponent implements OnInit {

  @Input() uscisFormsDateFormGroup: FormGroup;
  @Input() editionDateFormControl: FormControl;
  @Input() expirationDateFormControl: FormControl;
  @Input() index = null;
  @Input() submitted = false;


  constructor() {
  }

  ngOnInit() {

  }


}
