import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBookCopy } from '../book-copy.model';
import { BookCopyService } from '../service/book-copy.service';

const bookCopyResolve = (route: ActivatedRouteSnapshot): Observable<null | IBookCopy> => {
  const id = route.params['id'];
  if (id) {
    return inject(BookCopyService)
      .find(id)
      .pipe(
        mergeMap((bookCopy: HttpResponse<IBookCopy>) => {
          if (bookCopy.body) {
            return of(bookCopy.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default bookCopyResolve;
