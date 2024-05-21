import { IPublisher } from 'app/entities/publisher/publisher.model';
import { IBook } from 'app/entities/book/book.model';

export interface IBookCopy {
  id: number;
  yearPublished?: number | null;
  publisher?: Pick<IPublisher, 'id'> | null;
  book?: Pick<IBook, 'id'> | null;
}

export type NewBookCopy = Omit<IBookCopy, 'id'> & { id: null };
