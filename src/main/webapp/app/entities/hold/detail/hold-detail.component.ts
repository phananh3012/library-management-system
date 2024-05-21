import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IHold } from '../hold.model';

@Component({
  standalone: true,
  selector: 'jhi-hold-detail',
  templateUrl: './hold-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class HoldDetailComponent {
  hold = input<IHold | null>(null);

  previousState(): void {
    window.history.back();
  }
}
