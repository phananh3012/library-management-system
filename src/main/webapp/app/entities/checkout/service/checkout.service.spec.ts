import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICheckout } from '../checkout.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../checkout.test-samples';

import { CheckoutService, RestCheckout } from './checkout.service';

const requireRestSample: RestCheckout = {
  ...sampleWithRequiredData,
  startTime: sampleWithRequiredData.startTime?.toJSON(),
  endTime: sampleWithRequiredData.endTime?.toJSON(),
};

describe('Checkout Service', () => {
  let service: CheckoutService;
  let httpMock: HttpTestingController;
  let expectedResult: ICheckout | ICheckout[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CheckoutService);
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

    it('should create a Checkout', () => {
      const checkout = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(checkout).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Checkout', () => {
      const checkout = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(checkout).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Checkout', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Checkout', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Checkout', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCheckoutToCollectionIfMissing', () => {
      it('should add a Checkout to an empty array', () => {
        const checkout: ICheckout = sampleWithRequiredData;
        expectedResult = service.addCheckoutToCollectionIfMissing([], checkout);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(checkout);
      });

      it('should not add a Checkout to an array that contains it', () => {
        const checkout: ICheckout = sampleWithRequiredData;
        const checkoutCollection: ICheckout[] = [
          {
            ...checkout,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCheckoutToCollectionIfMissing(checkoutCollection, checkout);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Checkout to an array that doesn't contain it", () => {
        const checkout: ICheckout = sampleWithRequiredData;
        const checkoutCollection: ICheckout[] = [sampleWithPartialData];
        expectedResult = service.addCheckoutToCollectionIfMissing(checkoutCollection, checkout);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(checkout);
      });

      it('should add only unique Checkout to an array', () => {
        const checkoutArray: ICheckout[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const checkoutCollection: ICheckout[] = [sampleWithRequiredData];
        expectedResult = service.addCheckoutToCollectionIfMissing(checkoutCollection, ...checkoutArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const checkout: ICheckout = sampleWithRequiredData;
        const checkout2: ICheckout = sampleWithPartialData;
        expectedResult = service.addCheckoutToCollectionIfMissing([], checkout, checkout2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(checkout);
        expect(expectedResult).toContain(checkout2);
      });

      it('should accept null and undefined values', () => {
        const checkout: ICheckout = sampleWithRequiredData;
        expectedResult = service.addCheckoutToCollectionIfMissing([], null, checkout, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(checkout);
      });

      it('should return initial array if no Checkout is added', () => {
        const checkoutCollection: ICheckout[] = [sampleWithRequiredData];
        expectedResult = service.addCheckoutToCollectionIfMissing(checkoutCollection, undefined, null);
        expect(expectedResult).toEqual(checkoutCollection);
      });
    });

    describe('compareCheckout', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCheckout(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCheckout(entity1, entity2);
        const compareResult2 = service.compareCheckout(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCheckout(entity1, entity2);
        const compareResult2 = service.compareCheckout(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCheckout(entity1, entity2);
        const compareResult2 = service.compareCheckout(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
