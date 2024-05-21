import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPatronAccount } from 'app/entities/patron-account/patron-account.model';
import { PatronAccountService } from 'app/entities/patron-account/service/patron-account.service';
import { INotification } from '../notification.model';
import { NotificationService } from '../service/notification.service';
import { NotificationFormService, NotificationFormGroup } from './notification-form.service';

@Component({
  standalone: true,
  selector: 'jhi-notification-update',
  templateUrl: './notification-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class NotificationUpdateComponent implements OnInit {
  isSaving = false;
  notification: INotification | null = null;

  patronAccountsSharedCollection: IPatronAccount[] = [];

  protected notificationService = inject(NotificationService);
  protected notificationFormService = inject(NotificationFormService);
  protected patronAccountService = inject(PatronAccountService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: NotificationFormGroup = this.notificationFormService.createNotificationFormGroup();

  comparePatronAccount = (o1: IPatronAccount | null, o2: IPatronAccount | null): boolean =>
    this.patronAccountService.comparePatronAccount(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => {
      this.notification = notification;
      if (notification) {
        this.updateForm(notification);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notification = this.notificationFormService.getNotification(this.editForm);
    if (notification.id !== null) {
      this.subscribeToSaveResponse(this.notificationService.update(notification));
    } else {
      this.subscribeToSaveResponse(this.notificationService.create(notification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotification>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(notification: INotification): void {
    this.notification = notification;
    this.notificationFormService.resetForm(this.editForm, notification);

    this.patronAccountsSharedCollection = this.patronAccountService.addPatronAccountToCollectionIfMissing<IPatronAccount>(
      this.patronAccountsSharedCollection,
      notification.patronAccount,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.patronAccountService
      .query()
      .pipe(map((res: HttpResponse<IPatronAccount[]>) => res.body ?? []))
      .pipe(
        map((patronAccounts: IPatronAccount[]) =>
          this.patronAccountService.addPatronAccountToCollectionIfMissing<IPatronAccount>(patronAccounts, this.notification?.patronAccount),
        ),
      )
      .subscribe((patronAccounts: IPatronAccount[]) => (this.patronAccountsSharedCollection = patronAccounts));
  }
}
