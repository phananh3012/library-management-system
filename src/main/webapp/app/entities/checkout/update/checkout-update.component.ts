import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBookCopy } from 'app/entities/book-copy/book-copy.model';
import { BookCopyService } from 'app/entities/book-copy/service/book-copy.service';
import { IPatronAccount } from 'app/entities/patron-account/patron-account.model';
import { PatronAccountService } from 'app/entities/patron-account/service/patron-account.service';
import { CheckoutService } from '../service/checkout.service';
import { ICheckout } from '../checkout.model';
import { CheckoutFormService, CheckoutFormGroup } from './checkout-form.service';

@Component({
  standalone: true,
  selector: 'jhi-checkout-update',
  templateUrl: './checkout-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CheckoutUpdateComponent implements OnInit {
  isSaving = false;
  checkout: ICheckout | null = null;

  bookCopiesSharedCollection: IBookCopy[] = [];
  patronAccountsSharedCollection: IPatronAccount[] = [];

  protected checkoutService = inject(CheckoutService);
  protected checkoutFormService = inject(CheckoutFormService);
  protected bookCopyService = inject(BookCopyService);
  protected patronAccountService = inject(PatronAccountService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CheckoutFormGroup = this.checkoutFormService.createCheckoutFormGroup();

  compareBookCopy = (o1: IBookCopy | null, o2: IBookCopy | null): boolean => this.bookCopyService.compareBookCopy(o1, o2);

  comparePatronAccount = (o1: IPatronAccount | null, o2: IPatronAccount | null): boolean =>
    this.patronAccountService.comparePatronAccount(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ checkout }) => {
      this.checkout = checkout;
      if (checkout) {
        this.updateForm(checkout);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const checkout = this.checkoutFormService.getCheckout(this.editForm);
    if (checkout.id !== null) {
      this.subscribeToSaveResponse(this.checkoutService.update(checkout));
    } else {
      this.subscribeToSaveResponse(this.checkoutService.create(checkout));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICheckout>>): void {
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

  protected updateForm(checkout: ICheckout): void {
    this.checkout = checkout;
    this.checkoutFormService.resetForm(this.editForm, checkout);

    this.bookCopiesSharedCollection = this.bookCopyService.addBookCopyToCollectionIfMissing<IBookCopy>(
      this.bookCopiesSharedCollection,
      checkout.bookCopy,
    );
    this.patronAccountsSharedCollection = this.patronAccountService.addPatronAccountToCollectionIfMissing<IPatronAccount>(
      this.patronAccountsSharedCollection,
      checkout.patronAccount,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bookCopyService
      .query()
      .pipe(map((res: HttpResponse<IBookCopy[]>) => res.body ?? []))
      .pipe(
        map((bookCopies: IBookCopy[]) =>
          this.bookCopyService.addBookCopyToCollectionIfMissing<IBookCopy>(bookCopies, this.checkout?.bookCopy),
        ),
      )
      .subscribe((bookCopies: IBookCopy[]) => (this.bookCopiesSharedCollection = bookCopies));

    this.patronAccountService
      .query()
      .pipe(map((res: HttpResponse<IPatronAccount[]>) => res.body ?? []))
      .pipe(
        map((patronAccounts: IPatronAccount[]) =>
          this.patronAccountService.addPatronAccountToCollectionIfMissing<IPatronAccount>(patronAccounts, this.checkout?.patronAccount),
        ),
      )
      .subscribe((patronAccounts: IPatronAccount[]) => (this.patronAccountsSharedCollection = patronAccounts));
  }
}
