import { IAuthor, NewAuthor } from './author.model';

export const sampleWithRequiredData: IAuthor = {
  id: 18036,
};

export const sampleWithPartialData: IAuthor = {
  id: 30573,
};

export const sampleWithFullData: IAuthor = {
  id: 24204,
  name: 'gigantic decoder afore',
};

export const sampleWithNewData: NewAuthor = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
