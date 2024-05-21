import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IHold, NewHold } from '../hold.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHold for edit and NewHoldFormGroupInput for create.
 */
type HoldFormGroupInput = IHold | PartialWithRequiredKeyOf<NewHold>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IHold | NewHold> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

type HoldFormRawValue = FormValueOf<IHold>;

type NewHoldFormRawValue = FormValueOf<NewHold>;

type HoldFormDefaults = Pick<NewHold, 'id' | 'startTime' | 'endTime'>;

type HoldFormGroupContent = {
  id: FormControl<HoldFormRawValue['id'] | NewHold['id']>;
  startTime: FormControl<HoldFormRawValue['startTime']>;
  endTime: FormControl<HoldFormRawValue['endTime']>;
  bookCopy: FormControl<HoldFormRawValue['bookCopy']>;
  patronAccount: FormControl<HoldFormRawValue['patronAccount']>;
};

export type HoldFormGroup = FormGroup<HoldFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HoldFormService {
  createHoldFormGroup(hold: HoldFormGroupInput = { id: null }): HoldFormGroup {
    const holdRawValue = this.convertHoldToHoldRawValue({
      ...this.getFormDefaults(),
      ...hold,
    });
    return new FormGroup<HoldFormGroupContent>({
      id: new FormControl(
        { value: holdRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      startTime: new FormControl(holdRawValue.startTime),
      endTime: new FormControl(holdRawValue.endTime),
      bookCopy: new FormControl(holdRawValue.bookCopy),
      patronAccount: new FormControl(holdRawValue.patronAccount),
    });
  }

  getHold(form: HoldFormGroup): IHold | NewHold {
    return this.convertHoldRawValueToHold(form.getRawValue() as HoldFormRawValue | NewHoldFormRawValue);
  }

  resetForm(form: HoldFormGroup, hold: HoldFormGroupInput): void {
    const holdRawValue = this.convertHoldToHoldRawValue({ ...this.getFormDefaults(), ...hold });
    form.reset(
      {
        ...holdRawValue,
        id: { value: holdRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): HoldFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      endTime: currentTime,
    };
  }

  private convertHoldRawValueToHold(rawHold: HoldFormRawValue | NewHoldFormRawValue): IHold | NewHold {
    return {
      ...rawHold,
      startTime: dayjs(rawHold.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawHold.endTime, DATE_TIME_FORMAT),
    };
  }

  private convertHoldToHoldRawValue(
    hold: IHold | (Partial<NewHold> & HoldFormDefaults),
  ): HoldFormRawValue | PartialWithRequiredKeyOf<NewHoldFormRawValue> {
    return {
      ...hold,
      startTime: hold.startTime ? hold.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: hold.endTime ? hold.endTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
