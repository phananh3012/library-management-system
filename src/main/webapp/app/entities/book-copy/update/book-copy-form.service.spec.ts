import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../book-copy.test-samples';

import { BookCopyFormService } from './book-copy-form.service';

describe('BookCopy Form Service', () => {
  let service: BookCopyFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookCopyFormService);
  });

  describe('Service methods', () => {
    describe('createBookCopyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBookCopyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            yearPublished: expect.any(Object),
            publisher: expect.any(Object),
            book: expect.any(Object),
          }),
        );
      });

      it('passing IBookCopy should create a new form with FormGroup', () => {
        const formGroup = service.createBookCopyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            yearPublished: expect.any(Object),
            publisher: expect.any(Object),
            book: expect.any(Object),
          }),
        );
      });
    });

    describe('getBookCopy', () => {
      it('should return NewBookCopy for default BookCopy initial value', () => {
        const formGroup = service.createBookCopyFormGroup(sampleWithNewData);

        const bookCopy = service.getBookCopy(formGroup) as any;

        expect(bookCopy).toMatchObject(sampleWithNewData);
      });

      it('should return NewBookCopy for empty BookCopy initial value', () => {
        const formGroup = service.createBookCopyFormGroup();

        const bookCopy = service.getBookCopy(formGroup) as any;

        expect(bookCopy).toMatchObject({});
      });

      it('should return IBookCopy', () => {
        const formGroup = service.createBookCopyFormGroup(sampleWithRequiredData);

        const bookCopy = service.getBookCopy(formGroup) as any;

        expect(bookCopy).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBookCopy should not enable id FormControl', () => {
        const formGroup = service.createBookCopyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBookCopy should disable id FormControl', () => {
        const formGroup = service.createBookCopyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
