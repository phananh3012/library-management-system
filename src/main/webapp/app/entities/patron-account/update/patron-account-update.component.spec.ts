import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { PatronAccountService } from '../service/patron-account.service';
import { IPatronAccount } from '../patron-account.model';
import { PatronAccountFormService } from './patron-account-form.service';

import { PatronAccountUpdateComponent } from './patron-account-update.component';

describe('PatronAccount Management Update Component', () => {
  let comp: PatronAccountUpdateComponent;
  let fixture: ComponentFixture<PatronAccountUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let patronAccountFormService: PatronAccountFormService;
  let patronAccountService: PatronAccountService;
  let bookService: BookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, PatronAccountUpdateComponent],
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
      .overrideTemplate(PatronAccountUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PatronAccountUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    patronAccountFormService = TestBed.inject(PatronAccountFormService);
    patronAccountService = TestBed.inject(PatronAccountService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Book query and add missing value', () => {
      const patronAccount: IPatronAccount = { id: 456 };
      const books: IBook[] = [{ id: 12174 }];
      patronAccount.books = books;

      const bookCollection: IBook[] = [{ id: 23621 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [...books];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ patronAccount });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(expect.objectContaining),
      );
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const patronAccount: IPatronAccount = { id: 456 };
      const book: IBook = { id: 6956 };
      patronAccount.books = [book];

      activatedRoute.data = of({ patronAccount });
      comp.ngOnInit();

      expect(comp.booksSharedCollection).toContain(book);
      expect(comp.patronAccount).toEqual(patronAccount);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPatronAccount>>();
      const patronAccount = { id: 123 };
      jest.spyOn(patronAccountFormService, 'getPatronAccount').mockReturnValue(patronAccount);
      jest.spyOn(patronAccountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ patronAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: patronAccount }));
      saveSubject.complete();

      // THEN
      expect(patronAccountFormService.getPatronAccount).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(patronAccountService.update).toHaveBeenCalledWith(expect.objectContaining(patronAccount));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPatronAccount>>();
      const patronAccount = { id: 123 };
      jest.spyOn(patronAccountFormService, 'getPatronAccount').mockReturnValue({ id: null });
      jest.spyOn(patronAccountService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ patronAccount: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: patronAccount }));
      saveSubject.complete();

      // THEN
      expect(patronAccountFormService.getPatronAccount).toHaveBeenCalled();
      expect(patronAccountService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPatronAccount>>();
      const patronAccount = { id: 123 };
      jest.spyOn(patronAccountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ patronAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(patronAccountService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
