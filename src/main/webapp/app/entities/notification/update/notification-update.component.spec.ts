import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IPatronAccount } from 'app/entities/patron-account/patron-account.model';
import { PatronAccountService } from 'app/entities/patron-account/service/patron-account.service';
import { NotificationService } from '../service/notification.service';
import { INotification } from '../notification.model';
import { NotificationFormService } from './notification-form.service';

import { NotificationUpdateComponent } from './notification-update.component';

describe('Notification Management Update Component', () => {
  let comp: NotificationUpdateComponent;
  let fixture: ComponentFixture<NotificationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notificationFormService: NotificationFormService;
  let notificationService: NotificationService;
  let patronAccountService: PatronAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, NotificationUpdateComponent],
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
      .overrideTemplate(NotificationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotificationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificationFormService = TestBed.inject(NotificationFormService);
    notificationService = TestBed.inject(NotificationService);
    patronAccountService = TestBed.inject(PatronAccountService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call PatronAccount query and add missing value', () => {
      const notification: INotification = { id: 456 };
      const patronAccount: IPatronAccount = { id: 30569 };
      notification.patronAccount = patronAccount;

      const patronAccountCollection: IPatronAccount[] = [{ id: 11628 }];
      jest.spyOn(patronAccountService, 'query').mockReturnValue(of(new HttpResponse({ body: patronAccountCollection })));
      const additionalPatronAccounts = [patronAccount];
      const expectedCollection: IPatronAccount[] = [...additionalPatronAccounts, ...patronAccountCollection];
      jest.spyOn(patronAccountService, 'addPatronAccountToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(patronAccountService.query).toHaveBeenCalled();
      expect(patronAccountService.addPatronAccountToCollectionIfMissing).toHaveBeenCalledWith(
        patronAccountCollection,
        ...additionalPatronAccounts.map(expect.objectContaining),
      );
      expect(comp.patronAccountsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const notification: INotification = { id: 456 };
      const patronAccount: IPatronAccount = { id: 12582 };
      notification.patronAccount = patronAccount;

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(comp.patronAccountsSharedCollection).toContain(patronAccount);
      expect(comp.notification).toEqual(notification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotification>>();
      const notification = { id: 123 };
      jest.spyOn(notificationFormService, 'getNotification').mockReturnValue(notification);
      jest.spyOn(notificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notification }));
      saveSubject.complete();

      // THEN
      expect(notificationFormService.getNotification).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificationService.update).toHaveBeenCalledWith(expect.objectContaining(notification));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotification>>();
      const notification = { id: 123 };
      jest.spyOn(notificationFormService, 'getNotification').mockReturnValue({ id: null });
      jest.spyOn(notificationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notification }));
      saveSubject.complete();

      // THEN
      expect(notificationFormService.getNotification).toHaveBeenCalled();
      expect(notificationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotification>>();
      const notification = { id: 123 };
      jest.spyOn(notificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
