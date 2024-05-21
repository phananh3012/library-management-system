import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IPatronAccount } from '../patron-account.model';

@Component({
  standalone: true,
  selector: 'jhi-patron-account-detail',
  templateUrl: './patron-account-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PatronAccountDetailComponent {
  patronAccount = input<IPatronAccount | null>(null);

  previousState(): void {
    window.history.back();
  }
}
