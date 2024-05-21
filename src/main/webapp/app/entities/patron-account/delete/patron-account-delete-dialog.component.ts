import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPatronAccount } from '../patron-account.model';
import { PatronAccountService } from '../service/patron-account.service';

@Component({
  standalone: true,
  templateUrl: './patron-account-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PatronAccountDeleteDialogComponent {
  patronAccount?: IPatronAccount;

  protected patronAccountService = inject(PatronAccountService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.patronAccountService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
