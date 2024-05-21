import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IPatronAccount } from '../patron-account.model';
import { PatronAccountService } from '../service/patron-account.service';
import { PatronAccountFormService, PatronAccountFormGroup } from './patron-account-form.service';

@Component({
  standalone: true,
  selector: 'jhi-patron-account-update',
  templateUrl: './patron-account-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PatronAccountUpdateComponent implements OnInit {
  isSaving = false;
  patronAccount: IPatronAccount | null = null;

  booksSharedCollection: IBook[] = [];

  protected patronAccountService = inject(PatronAccountService);
  protected patronAccountFormService = inject(PatronAccountFormService);
  protected bookService = inject(BookService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PatronAccountFormGroup = this.patronAccountFormService.createPatronAccountFormGroup();

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ patronAccount }) => {
      this.patronAccount = patronAccount;
      if (patronAccount) {
        this.updateForm(patronAccount);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const patronAccount = this.patronAccountFormService.getPatronAccount(this.editForm);
    if (patronAccount.id !== null) {
      this.subscribeToSaveResponse(this.patronAccountService.update(patronAccount));
    } else {
      this.subscribeToSaveResponse(this.patronAccountService.create(patronAccount));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPatronAccount>>): void {
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

  protected updateForm(patronAccount: IPatronAccount): void {
    this.patronAccount = patronAccount;
    this.patronAccountFormService.resetForm(this.editForm, patronAccount);

    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing<IBook>(
      this.booksSharedCollection,
      ...(patronAccount.books ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, ...(this.patronAccount?.books ?? []))))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));
  }
}
