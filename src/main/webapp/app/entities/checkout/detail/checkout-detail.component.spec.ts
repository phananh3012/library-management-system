import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CheckoutDetailComponent } from './checkout-detail.component';

describe('Checkout Management Detail Component', () => {
  let comp: CheckoutDetailComponent;
  let fixture: ComponentFixture<CheckoutDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckoutDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CheckoutDetailComponent,
              resolve: { checkout: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CheckoutDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load checkout on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CheckoutDetailComponent);

      // THEN
      expect(instance.checkout()).toEqual(expect.objectContaining({ id: 123 }));
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
