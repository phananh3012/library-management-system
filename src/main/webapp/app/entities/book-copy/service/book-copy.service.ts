import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBookCopy, NewBookCopy } from '../book-copy.model';

export type PartialUpdateBookCopy = Partial<IBookCopy> & Pick<IBookCopy, 'id'>;

export type EntityResponseType = HttpResponse<IBookCopy>;
export type EntityArrayResponseType = HttpResponse<IBookCopy[]>;

@Injectable({ providedIn: 'root' })
export class BookCopyService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/book-copies');

  create(bookCopy: NewBookCopy): Observable<EntityResponseType> {
    return this.http.post<IBookCopy>(this.resourceUrl, bookCopy, { observe: 'response' });
  }

  update(bookCopy: IBookCopy): Observable<EntityResponseType> {
    return this.http.put<IBookCopy>(`${this.resourceUrl}/${this.getBookCopyIdentifier(bookCopy)}`, bookCopy, { observe: 'response' });
  }

  partialUpdate(bookCopy: PartialUpdateBookCopy): Observable<EntityResponseType> {
    return this.http.patch<IBookCopy>(`${this.resourceUrl}/${this.getBookCopyIdentifier(bookCopy)}`, bookCopy, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBookCopy>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBookCopy[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBookCopyIdentifier(bookCopy: Pick<IBookCopy, 'id'>): number {
    return bookCopy.id;
  }

  compareBookCopy(o1: Pick<IBookCopy, 'id'> | null, o2: Pick<IBookCopy, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookCopyIdentifier(o1) === this.getBookCopyIdentifier(o2) : o1 === o2;
  }

  addBookCopyToCollectionIfMissing<Type extends Pick<IBookCopy, 'id'>>(
    bookCopyCollection: Type[],
    ...bookCopiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookCopies: Type[] = bookCopiesToCheck.filter(isPresent);
    if (bookCopies.length > 0) {
      const bookCopyCollectionIdentifiers = bookCopyCollection.map(bookCopyItem => this.getBookCopyIdentifier(bookCopyItem));
      const bookCopiesToAdd = bookCopies.filter(bookCopyItem => {
        const bookCopyIdentifier = this.getBookCopyIdentifier(bookCopyItem);
        if (bookCopyCollectionIdentifiers.includes(bookCopyIdentifier)) {
          return false;
        }
        bookCopyCollectionIdentifiers.push(bookCopyIdentifier);
        return true;
      });
      return [...bookCopiesToAdd, ...bookCopyCollection];
    }
    return bookCopyCollection;
  }
}
