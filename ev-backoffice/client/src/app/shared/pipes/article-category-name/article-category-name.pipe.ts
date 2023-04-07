import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'articleCategoryPipe'})
export class ArticleCategoryNamePipe implements PipeTransform {
  transform(group: any) {
    const addNestedLevel = Array(group.nested * 2).fill( ' &#160;' ).join('');
    return `${addNestedLevel} ${group.name}`;
  }
}
