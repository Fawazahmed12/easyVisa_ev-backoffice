import { Pipe, PipeTransform } from '@angular/core';

import { OrganizationType } from '../../../core/models/organization-type.enum';
import { getRepresentativeLabel } from '../../utils/get-representative-label';

@Pipe({name: 'representativeType'})
export class RepresentativeTypePipe implements PipeTransform {
  transform(value: OrganizationType) {
    return getRepresentativeLabel(value);
  }
}
