import dayjs from 'dayjs/esm';

import { IHold, NewHold } from './hold.model';

export const sampleWithRequiredData: IHold = {
  id: 6416,
};

export const sampleWithPartialData: IHold = {
  id: 12790,
};

export const sampleWithFullData: IHold = {
  id: 31474,
  startTime: dayjs('2024-05-21T06:33'),
  endTime: dayjs('2024-05-20T22:56'),
};

export const sampleWithNewData: NewHold = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
