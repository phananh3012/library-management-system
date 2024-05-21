import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICheckout } from '../checkout.model';
import { CheckoutService } from '../service/checkout.service';

const checkoutResolve = (route: ActivatedRouteSnapshot): Observable<null | ICheckout> => {
  const id = route.params['id'];
  if (id) {
    return inject(CheckoutService)
      .find(id)
      .pipe(
        mergeMap((checkout: HttpResponse<ICheckout>) => {
          if (checkout.body) {
            return of(checkout.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default checkoutResolve;
