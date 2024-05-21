import { IBookCopy, NewBookCopy } from './book-copy.model';

export const sampleWithRequiredData: IBookCopy = {
  id: 3653,
};

export const sampleWithPartialData: IBookCopy = {
  id: 17991,
};

export const sampleWithFullData: IBookCopy = {
  id: 27727,
  yearPublished: 1033,
};

export const sampleWithNewData: NewBookCopy = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
