import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PatronAccountComponent } from './list/patron-account.component';
import { PatronAccountDetailComponent } from './detail/patron-account-detail.component';
import { PatronAccountUpdateComponent } from './update/patron-account-update.component';
import PatronAccountResolve from './route/patron-account-routing-resolve.service';

const patronAccountRoute: Routes = [
  {
    path: '',
    component: PatronAccountComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PatronAccountDetailComponent,
    resolve: {
      patronAccount: PatronAccountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PatronAccountUpdateComponent,
    resolve: {
      patronAccount: PatronAccountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PatronAccountUpdateComponent,
    resolve: {
      patronAccount: PatronAccountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default patronAccountRoute;
