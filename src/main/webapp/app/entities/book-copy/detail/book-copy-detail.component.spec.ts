import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { BookCopyDetailComponent } from './book-copy-detail.component';

describe('BookCopy Management Detail Component', () => {
  let comp: BookCopyDetailComponent;
  let fixture: ComponentFixture<BookCopyDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookCopyDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: BookCopyDetailComponent,
              resolve: { bookCopy: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BookCopyDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookCopyDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bookCopy on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BookCopyDetailComponent);

      // THEN
      expect(instance.bookCopy()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
