import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPatronAccount } from '../patron-account.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../patron-account.test-samples';

import { PatronAccountService } from './patron-account.service';

const requireRestSample: IPatronAccount = {
  ...sampleWithRequiredData,
};

describe('PatronAccount Service', () => {
  let service: PatronAccountService;
  let httpMock: HttpTestingController;
  let expectedResult: IPatronAccount | IPatronAccount[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PatronAccountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a PatronAccount', () => {
      const patronAccount = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(patronAccount).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PatronAccount', () => {
      const patronAccount = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(patronAccount).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PatronAccount', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PatronAccount', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PatronAccount', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPatronAccountToCollectionIfMissing', () => {
      it('should add a PatronAccount to an empty array', () => {
        const patronAccount: IPatronAccount = sampleWithRequiredData;
        expectedResult = service.addPatronAccountToCollectionIfMissing([], patronAccount);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(patronAccount);
      });

      it('should not add a PatronAccount to an array that contains it', () => {
        const patronAccount: IPatronAccount = sampleWithRequiredData;
        const patronAccountCollection: IPatronAccount[] = [
          {
            ...patronAccount,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPatronAccountToCollectionIfMissing(patronAccountCollection, patronAccount);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PatronAccount to an array that doesn't contain it", () => {
        const patronAccount: IPatronAccount = sampleWithRequiredData;
        const patronAccountCollection: IPatronAccount[] = [sampleWithPartialData];
        expectedResult = service.addPatronAccountToCollectionIfMissing(patronAccountCollection, patronAccount);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(patronAccount);
      });

      it('should add only unique PatronAccount to an array', () => {
        const patronAccountArray: IPatronAccount[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const patronAccountCollection: IPatronAccount[] = [sampleWithRequiredData];
        expectedResult = service.addPatronAccountToCollectionIfMissing(patronAccountCollection, ...patronAccountArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const patronAccount: IPatronAccount = sampleWithRequiredData;
        const patronAccount2: IPatronAccount = sampleWithPartialData;
        expectedResult = service.addPatronAccountToCollectionIfMissing([], patronAccount, patronAccount2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(patronAccount);
        expect(expectedResult).toContain(patronAccount2);
      });

      it('should accept null and undefined values', () => {
        const patronAccount: IPatronAccount = sampleWithRequiredData;
        expectedResult = service.addPatronAccountToCollectionIfMissing([], null, patronAccount, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(patronAccount);
      });

      it('should return initial array if no PatronAccount is added', () => {
        const patronAccountCollection: IPatronAccount[] = [sampleWithRequiredData];
        expectedResult = service.addPatronAccountToCollectionIfMissing(patronAccountCollection, undefined, null);
        expect(expectedResult).toEqual(patronAccountCollection);
      });
    });

    describe('comparePatronAccount', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePatronAccount(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePatronAccount(entity1, entity2);
        const compareResult2 = service.comparePatronAccount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePatronAccount(entity1, entity2);
        const compareResult2 = service.comparePatronAccount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePatronAccount(entity1, entity2);
        const compareResult2 = service.comparePatronAccount(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
