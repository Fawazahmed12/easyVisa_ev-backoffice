import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SubmitArticleGuardService } from '../../core/guards/submit-article-guard.service';

import { ArticlesComponent } from './articles.component';
import { ArticlesResolverService } from './articles-resolver.service';

const routes: Routes = [
  {
    path: '',
    component: ArticlesComponent,
    resolve: {
      ArticlesResolverService,
    }
  },
  {
    path: 'submit-article',
    loadChildren: () => import('./submit-article/submit-article.module').then(m => m.SubmitArticleModule),
    canDeactivate: [SubmitArticleGuardService]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ArticlesRoutingModule {
}
