import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPublisher } from 'app/entities/publisher/publisher.model';
import { PublisherService } from 'app/entities/publisher/service/publisher.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { BookCopyService } from '../service/book-copy.service';
import { IBookCopy } from '../book-copy.model';
import { BookCopyFormService, BookCopyFormGroup } from './book-copy-form.service';

@Component({
  standalone: true,
  selector: 'jhi-book-copy-update',
  templateUrl: './book-copy-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookCopyUpdateComponent implements OnInit {
  isSaving = false;
  bookCopy: IBookCopy | null = null;

  publishersSharedCollection: IPublisher[] = [];
  booksSharedCollection: IBook[] = [];

  protected bookCopyService = inject(BookCopyService);
  protected bookCopyFormService = inject(BookCopyFormService);
  protected publisherService = inject(PublisherService);
  protected bookService = inject(BookService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookCopyFormGroup = this.bookCopyFormService.createBookCopyFormGroup();

  comparePublisher = (o1: IPublisher | null, o2: IPublisher | null): boolean => this.publisherService.comparePublisher(o1, o2);

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookCopy }) => {
      this.bookCopy = bookCopy;
      if (bookCopy) {
        this.updateForm(bookCopy);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bookCopy = this.bookCopyFormService.getBookCopy(this.editForm);
    if (bookCopy.id !== null) {
      this.subscribeToSaveResponse(this.bookCopyService.update(bookCopy));
    } else {
      this.subscribeToSaveResponse(this.bookCopyService.create(bookCopy));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBookCopy>>): void {
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

  protected updateForm(bookCopy: IBookCopy): void {
    this.bookCopy = bookCopy;
    this.bookCopyFormService.resetForm(this.editForm, bookCopy);

    this.publishersSharedCollection = this.publisherService.addPublisherToCollectionIfMissing<IPublisher>(
      this.publishersSharedCollection,
      bookCopy.publisher,
    );
    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing<IBook>(this.booksSharedCollection, bookCopy.book);
  }

  protected loadRelationshipsOptions(): void {
    this.publisherService
      .query()
      .pipe(map((res: HttpResponse<IPublisher[]>) => res.body ?? []))
      .pipe(
        map((publishers: IPublisher[]) =>
          this.publisherService.addPublisherToCollectionIfMissing<IPublisher>(publishers, this.bookCopy?.publisher),
        ),
      )
      .subscribe((publishers: IPublisher[]) => (this.publishersSharedCollection = publishers));

    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.bookCopy?.book)))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));
  }
}
