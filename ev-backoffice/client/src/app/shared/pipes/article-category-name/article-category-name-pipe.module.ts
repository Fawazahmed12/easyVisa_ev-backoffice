import { NgModule } from '@angular/core';

import { ArticleCategoryNamePipe } from './article-category-name.pipe';


@NgModule({
  declarations: [
    ArticleCategoryNamePipe,
  ],
  exports: [
    ArticleCategoryNamePipe
  ]
})
export class ArticleCategoryNamePipeModule { }
