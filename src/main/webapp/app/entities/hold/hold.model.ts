import dayjs from 'dayjs/esm';
import { IBookCopy } from 'app/entities/book-copy/book-copy.model';
import { IPatronAccount } from 'app/entities/patron-account/patron-account.model';

export interface IHold {
  id: number;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  bookCopy?: Pick<IBookCopy, 'id'> | null;
  patronAccount?: Pick<IPatronAccount, 'id'> | null;
}

export type NewHold = Omit<IHold, 'id'> & { id: null };
