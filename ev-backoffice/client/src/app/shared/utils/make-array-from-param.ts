import { isArray } from 'lodash-es';

export function   makeArrayFromParam(value) {
  return isArray(value) ? value : [value];
}
