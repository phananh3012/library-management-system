import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IBookCopy } from 'app/entities/book-copy/book-copy.model';
import { BookCopyService } from 'app/entities/book-copy/service/book-copy.service';
import { IPatronAccount } from 'app/entities/patron-account/patron-account.model';
import { PatronAccountService } from 'app/entities/patron-account/service/patron-account.service';
import { IHold } from '../hold.model';
import { HoldService } from '../service/hold.service';
import { HoldFormService } from './hold-form.service';

import { HoldUpdateComponent } from './hold-update.component';

describe('Hold Management Update Component', () => {
  let comp: HoldUpdateComponent;
  let fixture: ComponentFixture<HoldUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let holdFormService: HoldFormService;
  let holdService: HoldService;
  let bookCopyService: BookCopyService;
  let patronAccountService: PatronAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, HoldUpdateComponent],
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
      .overrideTemplate(HoldUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HoldUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    holdFormService = TestBed.inject(HoldFormService);
    holdService = TestBed.inject(HoldService);
    bookCopyService = TestBed.inject(BookCopyService);
    patronAccountService = TestBed.inject(PatronAccountService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BookCopy query and add missing value', () => {
      const hold: IHold = { id: 456 };
      const bookCopy: IBookCopy = { id: 7096 };
      hold.bookCopy = bookCopy;

      const bookCopyCollection: IBookCopy[] = [{ id: 11168 }];
      jest.spyOn(bookCopyService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCopyCollection })));
      const additionalBookCopies = [bookCopy];
      const expectedCollection: IBookCopy[] = [...additionalBookCopies, ...bookCopyCollection];
      jest.spyOn(bookCopyService, 'addBookCopyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      expect(bookCopyService.query).toHaveBeenCalled();
      expect(bookCopyService.addBookCopyToCollectionIfMissing).toHaveBeenCalledWith(
        bookCopyCollection,
        ...additionalBookCopies.map(expect.objectContaining),
      );
      expect(comp.bookCopiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call PatronAccount query and add missing value', () => {
      const hold: IHold = { id: 456 };
      const patronAccount: IPatronAccount = { id: 14829 };
      hold.patronAccount = patronAccount;

      const patronAccountCollection: IPatronAccount[] = [{ id: 15246 }];
      jest.spyOn(patronAccountService, 'query').mockReturnValue(of(new HttpResponse({ body: patronAccountCollection })));
      const additionalPatronAccounts = [patronAccount];
      const expectedCollection: IPatronAccount[] = [...additionalPatronAccounts, ...patronAccountCollection];
      jest.spyOn(patronAccountService, 'addPatronAccountToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      expect(patronAccountService.query).toHaveBeenCalled();
      expect(patronAccountService.addPatronAccountToCollectionIfMissing).toHaveBeenCalledWith(
        patronAccountCollection,
        ...additionalPatronAccounts.map(expect.objectContaining),
      );
      expect(comp.patronAccountsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const hold: IHold = { id: 456 };
      const bookCopy: IBookCopy = { id: 16949 };
      hold.bookCopy = bookCopy;
      const patronAccount: IPatronAccount = { id: 21178 };
      hold.patronAccount = patronAccount;

      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      expect(comp.bookCopiesSharedCollection).toContain(bookCopy);
      expect(comp.patronAccountsSharedCollection).toContain(patronAccount);
      expect(comp.hold).toEqual(hold);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHold>>();
      const hold = { id: 123 };
      jest.spyOn(holdFormService, 'getHold').mockReturnValue(hold);
      jest.spyOn(holdService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hold }));
      saveSubject.complete();

      // THEN
      expect(holdFormService.getHold).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(holdService.update).toHaveBeenCalledWith(expect.objectContaining(hold));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHold>>();
      const hold = { id: 123 };
      jest.spyOn(holdFormService, 'getHold').mockReturnValue({ id: null });
      jest.spyOn(holdService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hold: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hold }));
      saveSubject.complete();

      // THEN
      expect(holdFormService.getHold).toHaveBeenCalled();
      expect(holdService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHold>>();
      const hold = { id: 123 };
      jest.spyOn(holdService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(holdService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBookCopy', () => {
      it('Should forward to bookCopyService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(bookCopyService, 'compareBookCopy');
        comp.compareBookCopy(entity, entity2);
        expect(bookCopyService.compareBookCopy).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePatronAccount', () => {
      it('Should forward to patronAccountService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(patronAccountService, 'comparePatronAccount');
        comp.comparePatronAccount(entity, entity2);
        expect(patronAccountService.comparePatronAccount).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
