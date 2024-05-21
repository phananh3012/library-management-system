import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPatronAccount, NewPatronAccount } from '../patron-account.model';

export type PartialUpdatePatronAccount = Partial<IPatronAccount> & Pick<IPatronAccount, 'id'>;

export type EntityResponseType = HttpResponse<IPatronAccount>;
export type EntityArrayResponseType = HttpResponse<IPatronAccount[]>;

@Injectable({ providedIn: 'root' })
export class PatronAccountService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/patron-accounts');

  create(patronAccount: NewPatronAccount): Observable<EntityResponseType> {
    return this.http.post<IPatronAccount>(this.resourceUrl, patronAccount, { observe: 'response' });
  }

  update(patronAccount: IPatronAccount): Observable<EntityResponseType> {
    return this.http.put<IPatronAccount>(`${this.resourceUrl}/${this.getPatronAccountIdentifier(patronAccount)}`, patronAccount, {
      observe: 'response',
    });
  }

  partialUpdate(patronAccount: PartialUpdatePatronAccount): Observable<EntityResponseType> {
    return this.http.patch<IPatronAccount>(`${this.resourceUrl}/${this.getPatronAccountIdentifier(patronAccount)}`, patronAccount, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPatronAccount>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPatronAccount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPatronAccountIdentifier(patronAccount: Pick<IPatronAccount, 'id'>): number {
    return patronAccount.id;
  }

  comparePatronAccount(o1: Pick<IPatronAccount, 'id'> | null, o2: Pick<IPatronAccount, 'id'> | null): boolean {
    return o1 && o2 ? this.getPatronAccountIdentifier(o1) === this.getPatronAccountIdentifier(o2) : o1 === o2;
  }

  addPatronAccountToCollectionIfMissing<Type extends Pick<IPatronAccount, 'id'>>(
    patronAccountCollection: Type[],
    ...patronAccountsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const patronAccounts: Type[] = patronAccountsToCheck.filter(isPresent);
    if (patronAccounts.length > 0) {
      const patronAccountCollectionIdentifiers = patronAccountCollection.map(patronAccountItem =>
        this.getPatronAccountIdentifier(patronAccountItem),
      );
      const patronAccountsToAdd = patronAccounts.filter(patronAccountItem => {
        const patronAccountIdentifier = this.getPatronAccountIdentifier(patronAccountItem);
        if (patronAccountCollectionIdentifiers.includes(patronAccountIdentifier)) {
          return false;
        }
        patronAccountCollectionIdentifiers.push(patronAccountIdentifier);
        return true;
      });
      return [...patronAccountsToAdd, ...patronAccountCollection];
    }
    return patronAccountCollection;
  }
}
