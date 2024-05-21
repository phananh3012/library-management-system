import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHold, NewHold } from '../hold.model';

export type PartialUpdateHold = Partial<IHold> & Pick<IHold, 'id'>;

type RestOf<T extends IHold | NewHold> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

export type RestHold = RestOf<IHold>;

export type NewRestHold = RestOf<NewHold>;

export type PartialUpdateRestHold = RestOf<PartialUpdateHold>;

export type EntityResponseType = HttpResponse<IHold>;
export type EntityArrayResponseType = HttpResponse<IHold[]>;

@Injectable({ providedIn: 'root' })
export class HoldService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/holds');

  create(hold: NewHold): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(hold);
    return this.http.post<RestHold>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(hold: IHold): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(hold);
    return this.http
      .put<RestHold>(`${this.resourceUrl}/${this.getHoldIdentifier(hold)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(hold: PartialUpdateHold): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(hold);
    return this.http
      .patch<RestHold>(`${this.resourceUrl}/${this.getHoldIdentifier(hold)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestHold>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestHold[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getHoldIdentifier(hold: Pick<IHold, 'id'>): number {
    return hold.id;
  }

  compareHold(o1: Pick<IHold, 'id'> | null, o2: Pick<IHold, 'id'> | null): boolean {
    return o1 && o2 ? this.getHoldIdentifier(o1) === this.getHoldIdentifier(o2) : o1 === o2;
  }

  addHoldToCollectionIfMissing<Type extends Pick<IHold, 'id'>>(
    holdCollection: Type[],
    ...holdsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const holds: Type[] = holdsToCheck.filter(isPresent);
    if (holds.length > 0) {
      const holdCollectionIdentifiers = holdCollection.map(holdItem => this.getHoldIdentifier(holdItem));
      const holdsToAdd = holds.filter(holdItem => {
        const holdIdentifier = this.getHoldIdentifier(holdItem);
        if (holdCollectionIdentifiers.includes(holdIdentifier)) {
          return false;
        }
        holdCollectionIdentifiers.push(holdIdentifier);
        return true;
      });
      return [...holdsToAdd, ...holdCollection];
    }
    return holdCollection;
  }

  protected convertDateFromClient<T extends IHold | NewHold | PartialUpdateHold>(hold: T): RestOf<T> {
    return {
      ...hold,
      startTime: hold.startTime?.toJSON() ?? null,
      endTime: hold.endTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restHold: RestHold): IHold {
    return {
      ...restHold,
      startTime: restHold.startTime ? dayjs(restHold.startTime) : undefined,
      endTime: restHold.endTime ? dayjs(restHold.endTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestHold>): HttpResponse<IHold> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestHold[]>): HttpResponse<IHold[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
