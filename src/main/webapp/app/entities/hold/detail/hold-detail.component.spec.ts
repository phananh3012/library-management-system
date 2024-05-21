import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { HoldDetailComponent } from './hold-detail.component';

describe('Hold Management Detail Component', () => {
  let comp: HoldDetailComponent;
  let fixture: ComponentFixture<HoldDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HoldDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: HoldDetailComponent,
              resolve: { hold: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(HoldDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HoldDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load hold on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', HoldDetailComponent);

      // THEN
      expect(instance.hold()).toEqual(expect.objectContaining({ id: 123 }));
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
