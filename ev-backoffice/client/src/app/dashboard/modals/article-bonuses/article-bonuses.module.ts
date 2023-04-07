import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { MiniProfileModule } from '../mini-profile/mini-profile.module';

import { ArticleBonusesComponent } from './article-bonuses.component';

@NgModule({
  imports: [
    SharedModule,
    MiniProfileModule,
  ],
  declarations: [
    ArticleBonusesComponent,
  ],
  exports: [
    ArticleBonusesComponent,
  ],
})

export class ArticleBonusesModule {
}
