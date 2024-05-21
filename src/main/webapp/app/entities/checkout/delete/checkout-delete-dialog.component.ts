import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICheckout } from '../checkout.model';
import { CheckoutService } from '../service/checkout.service';

@Component({
  standalone: true,
  templateUrl: './checkout-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CheckoutDeleteDialogComponent {
  checkout?: ICheckout;

  protected checkoutService = inject(CheckoutService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.checkoutService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
