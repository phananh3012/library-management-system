import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ICheckout } from '../checkout.model';

@Component({
  standalone: true,
  selector: 'jhi-checkout-detail',
  templateUrl: './checkout-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class CheckoutDetailComponent {
  checkout = input<ICheckout | null>(null);

  previousState(): void {
    window.history.back();
  }
}
