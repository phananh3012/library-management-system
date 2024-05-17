import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 13052,
  login: 'gZ-v',
};

export const sampleWithPartialData: IUser = {
  id: 30864,
  login: 'Ur2@drdaY\\"Rdop4\\\'bQio',
};

export const sampleWithFullData: IUser = {
  id: 30876,
  login: 'Hkzmis@gLSYA\\HLu0tA\\E9\\TeRgDs\\R7RK3Y\\S6Xl',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
