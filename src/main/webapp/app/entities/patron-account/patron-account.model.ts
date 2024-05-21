import { IBook } from 'app/entities/book/book.model';

export interface IPatronAccount {
  id: number;
  cardNumber?: string | null;
  firstName?: string | null;
  surname?: string | null;
  email?: string | null;
  status?: string | null;
  books?: Pick<IBook, 'id'>[] | null;
}

export type NewPatronAccount = Omit<IPatronAccount, 'id'> & { id: null };
