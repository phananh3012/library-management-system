import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBookCopy } from '../book-copy.model';
import { BookCopyService } from '../service/book-copy.service';

@Component({
  standalone: true,
  templateUrl: './book-copy-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BookCopyDeleteDialogComponent {
  bookCopy?: IBookCopy;

  protected bookCopyService = inject(BookCopyService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookCopyService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
