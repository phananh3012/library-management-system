import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 15791,
};

export const sampleWithPartialData: INotification = {
  id: 8700,
};

export const sampleWithFullData: INotification = {
  id: 23459,
  sentAt: dayjs('2024-05-20T23:10'),
  type: 'quill during after',
};

export const sampleWithNewData: NewNotification = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
