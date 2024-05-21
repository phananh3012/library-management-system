import dayjs from 'dayjs/esm';

import { ICheckout, NewCheckout } from './checkout.model';

export const sampleWithRequiredData: ICheckout = {
  id: 16628,
};

export const sampleWithPartialData: ICheckout = {
  id: 10658,
  startTime: dayjs('2024-05-20T13:57'),
};

export const sampleWithFullData: ICheckout = {
  id: 22125,
  startTime: dayjs('2024-05-21T01:15'),
  endTime: dayjs('2024-05-21T03:59'),
  isReturned: false,
};

export const sampleWithNewData: NewCheckout = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
