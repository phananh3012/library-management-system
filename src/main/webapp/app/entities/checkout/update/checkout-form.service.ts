import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICheckout, NewCheckout } from '../checkout.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICheckout for edit and NewCheckoutFormGroupInput for create.
 */
type CheckoutFormGroupInput = ICheckout | PartialWithRequiredKeyOf<NewCheckout>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICheckout | NewCheckout> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

type CheckoutFormRawValue = FormValueOf<ICheckout>;

type NewCheckoutFormRawValue = FormValueOf<NewCheckout>;

type CheckoutFormDefaults = Pick<NewCheckout, 'id' | 'startTime' | 'endTime' | 'isReturned'>;

type CheckoutFormGroupContent = {
  id: FormControl<CheckoutFormRawValue['id'] | NewCheckout['id']>;
  startTime: FormControl<CheckoutFormRawValue['startTime']>;
  endTime: FormControl<CheckoutFormRawValue['endTime']>;
  isReturned: FormControl<CheckoutFormRawValue['isReturned']>;
  bookCopy: FormControl<CheckoutFormRawValue['bookCopy']>;
  patronAccount: FormControl<CheckoutFormRawValue['patronAccount']>;
};

export type CheckoutFormGroup = FormGroup<CheckoutFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CheckoutFormService {
  createCheckoutFormGroup(checkout: CheckoutFormGroupInput = { id: null }): CheckoutFormGroup {
    const checkoutRawValue = this.convertCheckoutToCheckoutRawValue({
      ...this.getFormDefaults(),
      ...checkout,
    });
    return new FormGroup<CheckoutFormGroupContent>({
      id: new FormControl(
        { value: checkoutRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      startTime: new FormControl(checkoutRawValue.startTime),
      endTime: new FormControl(checkoutRawValue.endTime),
      isReturned: new FormControl(checkoutRawValue.isReturned),
      bookCopy: new FormControl(checkoutRawValue.bookCopy),
      patronAccount: new FormControl(checkoutRawValue.patronAccount),
    });
  }

  getCheckout(form: CheckoutFormGroup): ICheckout | NewCheckout {
    return this.convertCheckoutRawValueToCheckout(form.getRawValue() as CheckoutFormRawValue | NewCheckoutFormRawValue);
  }

  resetForm(form: CheckoutFormGroup, checkout: CheckoutFormGroupInput): void {
    const checkoutRawValue = this.convertCheckoutToCheckoutRawValue({ ...this.getFormDefaults(), ...checkout });
    form.reset(
      {
        ...checkoutRawValue,
        id: { value: checkoutRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CheckoutFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      endTime: currentTime,
      isReturned: false,
    };
  }

  private convertCheckoutRawValueToCheckout(rawCheckout: CheckoutFormRawValue | NewCheckoutFormRawValue): ICheckout | NewCheckout {
    return {
      ...rawCheckout,
      startTime: dayjs(rawCheckout.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawCheckout.endTime, DATE_TIME_FORMAT),
    };
  }

  private convertCheckoutToCheckoutRawValue(
    checkout: ICheckout | (Partial<NewCheckout> & CheckoutFormDefaults),
  ): CheckoutFormRawValue | PartialWithRequiredKeyOf<NewCheckoutFormRawValue> {
    return {
      ...checkout,
      startTime: checkout.startTime ? checkout.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: checkout.endTime ? checkout.endTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
