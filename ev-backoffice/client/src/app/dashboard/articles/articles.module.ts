import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { SelectRepresentativeHeaderModule } from '../../components/select-representative-header/select-representative-header.module';
import { TableModule } from '../../components/table/table.module';
import { SafeHtmlPipeModule } from '../../shared/pipes/safe-html/safeHtmlPipe.module';
import { PaginationModule } from '../../components/pagination/pagination.module';
import { SpinnerModule } from '../../components/spinner/spinner.module';

import { SelectAffiliationModule } from '../components/select-affiliation/select-affiliation.module';

import { ArticlesRoutingModule } from './articles-routing.module';
import { ArticlesComponent } from './articles.component';
import { ArticlesResolverService } from './articles-resolver.service';
import { SubmitArticleModule } from './submit-article/submit-article.module';


@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    TableModule,
    ArticlesRoutingModule,
    SelectRepresentativeHeaderModule,
    SelectAffiliationModule,
    SafeHtmlPipeModule,
    SubmitArticleModule,
    PaginationModule,
    SpinnerModule,
  ],
  declarations: [ ArticlesComponent ],
  providers: [
    ArticlesResolverService,
    DatePipe,
  ]
})
export class ArticlesModule {
}
