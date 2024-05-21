import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IPublisher } from 'app/entities/publisher/publisher.model';
import { PublisherService } from 'app/entities/publisher/service/publisher.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IBookCopy } from '../book-copy.model';
import { BookCopyService } from '../service/book-copy.service';
import { BookCopyFormService } from './book-copy-form.service';

import { BookCopyUpdateComponent } from './book-copy-update.component';

describe('BookCopy Management Update Component', () => {
  let comp: BookCopyUpdateComponent;
  let fixture: ComponentFixture<BookCopyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookCopyFormService: BookCopyFormService;
  let bookCopyService: BookCopyService;
  let publisherService: PublisherService;
  let bookService: BookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, BookCopyUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BookCopyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookCopyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookCopyFormService = TestBed.inject(BookCopyFormService);
    bookCopyService = TestBed.inject(BookCopyService);
    publisherService = TestBed.inject(PublisherService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Publisher query and add missing value', () => {
      const bookCopy: IBookCopy = { id: 456 };
      const publisher: IPublisher = { id: 8739 };
      bookCopy.publisher = publisher;

      const publisherCollection: IPublisher[] = [{ id: 12100 }];
      jest.spyOn(publisherService, 'query').mockReturnValue(of(new HttpResponse({ body: publisherCollection })));
      const additionalPublishers = [publisher];
      const expectedCollection: IPublisher[] = [...additionalPublishers, ...publisherCollection];
      jest.spyOn(publisherService, 'addPublisherToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      expect(publisherService.query).toHaveBeenCalled();
      expect(publisherService.addPublisherToCollectionIfMissing).toHaveBeenCalledWith(
        publisherCollection,
        ...additionalPublishers.map(expect.objectContaining),
      );
      expect(comp.publishersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Book query and add missing value', () => {
      const bookCopy: IBookCopy = { id: 456 };
      const book: IBook = { id: 31598 };
      bookCopy.book = book;

      const bookCollection: IBook[] = [{ id: 7593 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(expect.objectContaining),
      );
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const bookCopy: IBookCopy = { id: 456 };
      const publisher: IPublisher = { id: 20436 };
      bookCopy.publisher = publisher;
      const book: IBook = { id: 6880 };
      bookCopy.book = book;

      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      expect(comp.publishersSharedCollection).toContain(publisher);
      expect(comp.booksSharedCollection).toContain(book);
      expect(comp.bookCopy).toEqual(bookCopy);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookCopy>>();
      const bookCopy = { id: 123 };
      jest.spyOn(bookCopyFormService, 'getBookCopy').mockReturnValue(bookCopy);
      jest.spyOn(bookCopyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookCopy }));
      saveSubject.complete();

      // THEN
      expect(bookCopyFormService.getBookCopy).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookCopyService.update).toHaveBeenCalledWith(expect.objectContaining(bookCopy));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookCopy>>();
      const bookCopy = { id: 123 };
      jest.spyOn(bookCopyFormService, 'getBookCopy').mockReturnValue({ id: null });
      jest.spyOn(bookCopyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCopy: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookCopy }));
      saveSubject.complete();

      // THEN
      expect(bookCopyFormService.getBookCopy).toHaveBeenCalled();
      expect(bookCopyService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookCopy>>();
      const bookCopy = { id: 123 };
      jest.spyOn(bookCopyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookCopyService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePublisher', () => {
      it('Should forward to publisherService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(publisherService, 'comparePublisher');
        comp.comparePublisher(entity, entity2);
        expect(publisherService.comparePublisher).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBook', () => {
      it('Should forward to bookService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(bookService, 'compareBook');
        comp.compareBook(entity, entity2);
        expect(bookService.compareBook).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
