import { NgModule } from '@angular/core';

import { QuillModule } from 'ngx-quill';

import { SharedModule } from '../../../shared/shared.module';
import { ArticleCategoryNamePipeModule } from '../../../shared/pipes/article-category-name/article-category-name-pipe.module';
import { WarningPersonalPageModule } from '../../../components/warning-personal-page/warning-personal-page.module';
import { SpinnerModule } from '../../../components/spinner/spinner.module';

import { ArticleBonusesModule } from '../../modals/article-bonuses/article-bonuses.module';

import { SubmitArticleComponent } from './submit-article.component';
import { SubmitArticleRoutingModule } from './submit-article-routing.module';


@NgModule({
  imports: [
    SharedModule,
    SubmitArticleRoutingModule,
    QuillModule,
    ArticleBonusesModule,
    ArticleCategoryNamePipeModule,
    SpinnerModule,
    WarningPersonalPageModule
  ],
  declarations: [
    SubmitArticleComponent,
  ],
  exports: [
    SubmitArticleComponent,
  ]
})
export class SubmitArticleModule {
}
