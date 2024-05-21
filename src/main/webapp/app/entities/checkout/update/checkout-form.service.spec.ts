import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../checkout.test-samples';

import { CheckoutFormService } from './checkout-form.service';

describe('Checkout Form Service', () => {
  let service: CheckoutFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CheckoutFormService);
  });

  describe('Service methods', () => {
    describe('createCheckoutFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCheckoutFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startTime: expect.any(Object),
            endTime: expect.any(Object),
            isReturned: expect.any(Object),
            bookCopy: expect.any(Object),
            patronAccount: expect.any(Object),
          }),
        );
      });

      it('passing ICheckout should create a new form with FormGroup', () => {
        const formGroup = service.createCheckoutFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startTime: expect.any(Object),
            endTime: expect.any(Object),
            isReturned: expect.any(Object),
            bookCopy: expect.any(Object),
            patronAccount: expect.any(Object),
          }),
        );
      });
    });

    describe('getCheckout', () => {
      it('should return NewCheckout for default Checkout initial value', () => {
        const formGroup = service.createCheckoutFormGroup(sampleWithNewData);

        const checkout = service.getCheckout(formGroup) as any;

        expect(checkout).toMatchObject(sampleWithNewData);
      });

      it('should return NewCheckout for empty Checkout initial value', () => {
        const formGroup = service.createCheckoutFormGroup();

        const checkout = service.getCheckout(formGroup) as any;

        expect(checkout).toMatchObject({});
      });

      it('should return ICheckout', () => {
        const formGroup = service.createCheckoutFormGroup(sampleWithRequiredData);

        const checkout = service.getCheckout(formGroup) as any;

        expect(checkout).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICheckout should not enable id FormControl', () => {
        const formGroup = service.createCheckoutFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCheckout should disable id FormControl', () => {
        const formGroup = service.createCheckoutFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
