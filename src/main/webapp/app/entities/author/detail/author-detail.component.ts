import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IAuthor } from '../author.model';

@Component({
  standalone: true,
  selector: 'jhi-author-detail',
  templateUrl: './author-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class AuthorDetailComponent {
  author = input<IAuthor | null>(null);

  previousState(): void {
    window.history.back();
  }
}
