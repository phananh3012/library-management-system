import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 19450,
};

export const sampleWithPartialData: ICategory = {
  id: 24457,
  name: 'diver',
};

export const sampleWithFullData: ICategory = {
  id: 4920,
  name: 'recite prime hitch',
};

export const sampleWithNewData: NewCategory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
