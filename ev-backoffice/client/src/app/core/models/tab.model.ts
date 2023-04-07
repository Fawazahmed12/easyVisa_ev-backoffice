export interface Tab {
  title: string;
  link: string[];
  disabled?: boolean;
  count?: number;
  packagesCount?: string;
  showExclamation?: boolean;
  class?: string;
  hide?: boolean;
}
