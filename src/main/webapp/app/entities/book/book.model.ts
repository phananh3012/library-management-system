import { IPatronAccount } from 'app/entities/patron-account/patron-account.model';
import { IAuthor } from 'app/entities/author/author.model';
import { ICategory } from 'app/entities/category/category.model';

export interface IBook {
  id: number;
  title?: string | null;
  patronAccounts?: Pick<IPatronAccount, 'id'>[] | null;
  authors?: Pick<IAuthor, 'id'>[] | null;
  category?: Pick<ICategory, 'id'> | null;
}

export type NewBook = Omit<IBook, 'id'> & { id: null };
