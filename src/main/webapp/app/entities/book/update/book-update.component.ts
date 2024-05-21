import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPatronAccount } from 'app/entities/patron-account/patron-account.model';
import { PatronAccountService } from 'app/entities/patron-account/service/patron-account.service';
import { IAuthor } from 'app/entities/author/author.model';
import { AuthorService } from 'app/entities/author/service/author.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { BookService } from '../service/book.service';
import { IBook } from '../book.model';
import { BookFormService, BookFormGroup } from './book-form.service';

@Component({
  standalone: true,
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookUpdateComponent implements OnInit {
  isSaving = false;
  book: IBook | null = null;

  patronAccountsSharedCollection: IPatronAccount[] = [];
  authorsSharedCollection: IAuthor[] = [];
  categoriesSharedCollection: ICategory[] = [];

  protected bookService = inject(BookService);
  protected bookFormService = inject(BookFormService);
  protected patronAccountService = inject(PatronAccountService);
  protected authorService = inject(AuthorService);
  protected categoryService = inject(CategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookFormGroup = this.bookFormService.createBookFormGroup();

  comparePatronAccount = (o1: IPatronAccount | null, o2: IPatronAccount | null): boolean =>
    this.patronAccountService.comparePatronAccount(o1, o2);

  compareAuthor = (o1: IAuthor | null, o2: IAuthor | null): boolean => this.authorService.compareAuthor(o1, o2);

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ book }) => {
      this.book = book;
      if (book) {
        this.updateForm(book);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const book = this.bookFormService.getBook(this.editForm);
    if (book.id !== null) {
      this.subscribeToSaveResponse(this.bookService.update(book));
    } else {
      this.subscribeToSaveResponse(this.bookService.create(book));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>): void {
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

  protected updateForm(book: IBook): void {
    this.book = book;
    this.bookFormService.resetForm(this.editForm, book);

    this.patronAccountsSharedCollection = this.patronAccountService.addPatronAccountToCollectionIfMissing<IPatronAccount>(
      this.patronAccountsSharedCollection,
      ...(book.patronAccounts ?? []),
    );
    this.authorsSharedCollection = this.authorService.addAuthorToCollectionIfMissing<IAuthor>(
      this.authorsSharedCollection,
      ...(book.authors ?? []),
    );
    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing<ICategory>(
      this.categoriesSharedCollection,
      book.category,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.patronAccountService
      .query()
      .pipe(map((res: HttpResponse<IPatronAccount[]>) => res.body ?? []))
      .pipe(
        map((patronAccounts: IPatronAccount[]) =>
          this.patronAccountService.addPatronAccountToCollectionIfMissing<IPatronAccount>(
            patronAccounts,
            ...(this.book?.patronAccounts ?? []),
          ),
        ),
      )
      .subscribe((patronAccounts: IPatronAccount[]) => (this.patronAccountsSharedCollection = patronAccounts));

    this.authorService
      .query()
      .pipe(map((res: HttpResponse<IAuthor[]>) => res.body ?? []))
      .pipe(map((authors: IAuthor[]) => this.authorService.addAuthorToCollectionIfMissing<IAuthor>(authors, ...(this.book?.authors ?? []))))
      .subscribe((authors: IAuthor[]) => (this.authorsSharedCollection = authors));

    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) => this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, this.book?.category)),
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));
  }
}
