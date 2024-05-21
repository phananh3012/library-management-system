import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IBookCopy } from '../book-copy.model';

@Component({
  standalone: true,
  selector: 'jhi-book-copy-detail',
  templateUrl: './book-copy-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class BookCopyDetailComponent {
  bookCopy = input<IBookCopy | null>(null);

  previousState(): void {
    window.history.back();
  }
}
