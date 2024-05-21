import { IBook, NewBook } from './book.model';

export const sampleWithRequiredData: IBook = {
  id: 17755,
};

export const sampleWithPartialData: IBook = {
  id: 10119,
};

export const sampleWithFullData: IBook = {
  id: 29885,
  title: 'uh-huh',
};

export const sampleWithNewData: NewBook = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
