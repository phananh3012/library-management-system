import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPatronAccount, NewPatronAccount } from '../patron-account.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPatronAccount for edit and NewPatronAccountFormGroupInput for create.
 */
type PatronAccountFormGroupInput = IPatronAccount | PartialWithRequiredKeyOf<NewPatronAccount>;

type PatronAccountFormDefaults = Pick<NewPatronAccount, 'id' | 'books'>;

type PatronAccountFormGroupContent = {
  id: FormControl<IPatronAccount['id'] | NewPatronAccount['id']>;
  cardNumber: FormControl<IPatronAccount['cardNumber']>;
  firstName: FormControl<IPatronAccount['firstName']>;
  surname: FormControl<IPatronAccount['surname']>;
  email: FormControl<IPatronAccount['email']>;
  status: FormControl<IPatronAccount['status']>;
  books: FormControl<IPatronAccount['books']>;
};

export type PatronAccountFormGroup = FormGroup<PatronAccountFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PatronAccountFormService {
  createPatronAccountFormGroup(patronAccount: PatronAccountFormGroupInput = { id: null }): PatronAccountFormGroup {
    const patronAccountRawValue = {
      ...this.getFormDefaults(),
      ...patronAccount,
    };
    return new FormGroup<PatronAccountFormGroupContent>({
      id: new FormControl(
        { value: patronAccountRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      cardNumber: new FormControl(patronAccountRawValue.cardNumber),
      firstName: new FormControl(patronAccountRawValue.firstName),
      surname: new FormControl(patronAccountRawValue.surname),
      email: new FormControl(patronAccountRawValue.email),
      status: new FormControl(patronAccountRawValue.status),
      books: new FormControl(patronAccountRawValue.books ?? []),
    });
  }

  getPatronAccount(form: PatronAccountFormGroup): IPatronAccount | NewPatronAccount {
    return form.getRawValue() as IPatronAccount | NewPatronAccount;
  }

  resetForm(form: PatronAccountFormGroup, patronAccount: PatronAccountFormGroupInput): void {
    const patronAccountRawValue = { ...this.getFormDefaults(), ...patronAccount };
    form.reset(
      {
        ...patronAccountRawValue,
        id: { value: patronAccountRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PatronAccountFormDefaults {
    return {
      id: null,
      books: [],
    };
  }
}
