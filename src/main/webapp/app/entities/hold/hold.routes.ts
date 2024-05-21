import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { HoldComponent } from './list/hold.component';
import { HoldDetailComponent } from './detail/hold-detail.component';
import { HoldUpdateComponent } from './update/hold-update.component';
import HoldResolve from './route/hold-routing-resolve.service';

const holdRoute: Routes = [
  {
    path: '',
    component: HoldComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HoldDetailComponent,
    resolve: {
      hold: HoldResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HoldUpdateComponent,
    resolve: {
      hold: HoldResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HoldUpdateComponent,
    resolve: {
      hold: HoldResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default holdRoute;
