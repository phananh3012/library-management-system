import dayjs from 'dayjs/esm';
import { IPatronAccount } from 'app/entities/patron-account/patron-account.model';

export interface INotification {
  id: number;
  sentAt?: dayjs.Dayjs | null;
  type?: string | null;
  patronAccount?: Pick<IPatronAccount, 'id'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
