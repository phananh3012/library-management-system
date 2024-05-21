import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICheckout, NewCheckout } from '../checkout.model';

export type PartialUpdateCheckout = Partial<ICheckout> & Pick<ICheckout, 'id'>;

type RestOf<T extends ICheckout | NewCheckout> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

export type RestCheckout = RestOf<ICheckout>;

export type NewRestCheckout = RestOf<NewCheckout>;

export type PartialUpdateRestCheckout = RestOf<PartialUpdateCheckout>;

export type EntityResponseType = HttpResponse<ICheckout>;
export type EntityArrayResponseType = HttpResponse<ICheckout[]>;

@Injectable({ providedIn: 'root' })
export class CheckoutService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/checkouts');

  create(checkout: NewCheckout): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(checkout);
    return this.http
      .post<RestCheckout>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(checkout: ICheckout): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(checkout);
    return this.http
      .put<RestCheckout>(`${this.resourceUrl}/${this.getCheckoutIdentifier(checkout)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(checkout: PartialUpdateCheckout): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(checkout);
    return this.http
      .patch<RestCheckout>(`${this.resourceUrl}/${this.getCheckoutIdentifier(checkout)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCheckout>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCheckout[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCheckoutIdentifier(checkout: Pick<ICheckout, 'id'>): number {
    return checkout.id;
  }

  compareCheckout(o1: Pick<ICheckout, 'id'> | null, o2: Pick<ICheckout, 'id'> | null): boolean {
    return o1 && o2 ? this.getCheckoutIdentifier(o1) === this.getCheckoutIdentifier(o2) : o1 === o2;
  }

  addCheckoutToCollectionIfMissing<Type extends Pick<ICheckout, 'id'>>(
    checkoutCollection: Type[],
    ...checkoutsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const checkouts: Type[] = checkoutsToCheck.filter(isPresent);
    if (checkouts.length > 0) {
      const checkoutCollectionIdentifiers = checkoutCollection.map(checkoutItem => this.getCheckoutIdentifier(checkoutItem));
      const checkoutsToAdd = checkouts.filter(checkoutItem => {
        const checkoutIdentifier = this.getCheckoutIdentifier(checkoutItem);
        if (checkoutCollectionIdentifiers.includes(checkoutIdentifier)) {
          return false;
        }
        checkoutCollectionIdentifiers.push(checkoutIdentifier);
        return true;
      });
      return [...checkoutsToAdd, ...checkoutCollection];
    }
    return checkoutCollection;
  }

  protected convertDateFromClient<T extends ICheckout | NewCheckout | PartialUpdateCheckout>(checkout: T): RestOf<T> {
    return {
      ...checkout,
      startTime: checkout.startTime?.toJSON() ?? null,
      endTime: checkout.endTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCheckout: RestCheckout): ICheckout {
    return {
      ...restCheckout,
      startTime: restCheckout.startTime ? dayjs(restCheckout.startTime) : undefined,
      endTime: restCheckout.endTime ? dayjs(restCheckout.endTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCheckout>): HttpResponse<ICheckout> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCheckout[]>): HttpResponse<ICheckout[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
