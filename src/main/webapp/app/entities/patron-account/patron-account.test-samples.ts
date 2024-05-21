import { IPatronAccount, NewPatronAccount } from './patron-account.model';

export const sampleWithRequiredData: IPatronAccount = {
  id: 14322,
};

export const sampleWithPartialData: IPatronAccount = {
  id: 28831,
  cardNumber: 'whereas',
  firstName: 'Anissa',
  surname: 'against obnoxiously lode',
};

export const sampleWithFullData: IPatronAccount = {
  id: 23050,
  cardNumber: 'cheerfully downright',
  firstName: 'Delpha',
  surname: 'ajar opposite',
  email: 'Jewell.Weber@gmail.com',
  status: 'when offensive',
};

export const sampleWithNewData: NewPatronAccount = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
