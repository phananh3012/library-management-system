import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '835924f3-10fb-4106-9528-d1428d99af54',
};

export const sampleWithPartialData: IAuthority = {
  name: '8e02d9d7-de21-47d0-ad7f-8ccac9fcf414',
};

export const sampleWithFullData: IAuthority = {
  name: 'b9db323b-13d1-4a43-9064-2c20393e80e4',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
