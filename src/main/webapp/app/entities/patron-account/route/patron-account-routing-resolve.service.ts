import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPatronAccount } from '../patron-account.model';
import { PatronAccountService } from '../service/patron-account.service';

const patronAccountResolve = (route: ActivatedRouteSnapshot): Observable<null | IPatronAccount> => {
  const id = route.params['id'];
  if (id) {
    return inject(PatronAccountService)
      .find(id)
      .pipe(
        mergeMap((patronAccount: HttpResponse<IPatronAccount>) => {
          if (patronAccount.body) {
            return of(patronAccount.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default patronAccountResolve;
