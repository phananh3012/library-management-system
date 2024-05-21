import { IPublisher, NewPublisher } from './publisher.model';

export const sampleWithRequiredData: IPublisher = {
  id: 16496,
};

export const sampleWithPartialData: IPublisher = {
  id: 15506,
  name: 'till',
};

export const sampleWithFullData: IPublisher = {
  id: 14624,
  name: 'welcome',
};

export const sampleWithNewData: NewPublisher = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
