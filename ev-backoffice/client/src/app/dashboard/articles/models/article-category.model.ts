export class ArticleCategory {
  tid: string;
  name: string;
  parent_target_id: string;
  nested: string;
  children: ArticleCategory[];
}

export class ArticleCategoryInitial {
  name: string;
  parent_target_id: string;
  tid: string;
}
