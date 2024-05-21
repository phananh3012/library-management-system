import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBookCopy, NewBookCopy } from '../book-copy.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookCopy for edit and NewBookCopyFormGroupInput for create.
 */
type BookCopyFormGroupInput = IBookCopy | PartialWithRequiredKeyOf<NewBookCopy>;

type BookCopyFormDefaults = Pick<NewBookCopy, 'id'>;

type BookCopyFormGroupContent = {
  id: FormControl<IBookCopy['id'] | NewBookCopy['id']>;
  yearPublished: FormControl<IBookCopy['yearPublished']>;
  publisher: FormControl<IBookCopy['publisher']>;
  book: FormControl<IBookCopy['book']>;
};

export type BookCopyFormGroup = FormGroup<BookCopyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookCopyFormService {
  createBookCopyFormGroup(bookCopy: BookCopyFormGroupInput = { id: null }): BookCopyFormGroup {
    const bookCopyRawValue = {
      ...this.getFormDefaults(),
      ...bookCopy,
    };
    return new FormGroup<BookCopyFormGroupContent>({
      id: new FormControl(
        { value: bookCopyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      yearPublished: new FormControl(bookCopyRawValue.yearPublished),
      publisher: new FormControl(bookCopyRawValue.publisher),
      book: new FormControl(bookCopyRawValue.book),
    });
  }

  getBookCopy(form: BookCopyFormGroup): IBookCopy | NewBookCopy {
    return form.getRawValue() as IBookCopy | NewBookCopy;
  }

  resetForm(form: BookCopyFormGroup, bookCopy: BookCopyFormGroupInput): void {
    const bookCopyRawValue = { ...this.getFormDefaults(), ...bookCopy };
    form.reset(
      {
        ...bookCopyRawValue,
        id: { value: bookCopyRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookCopyFormDefaults {
    return {
      id: null,
    };
  }
}
