import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CheckoutComponent } from './list/checkout.component';
import { CheckoutDetailComponent } from './detail/checkout-detail.component';
import { CheckoutUpdateComponent } from './update/checkout-update.component';
import CheckoutResolve from './route/checkout-routing-resolve.service';

const checkoutRoute: Routes = [
  {
    path: '',
    component: CheckoutComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CheckoutDetailComponent,
    resolve: {
      checkout: CheckoutResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CheckoutUpdateComponent,
    resolve: {
      checkout: CheckoutResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CheckoutUpdateComponent,
    resolve: {
      checkout: CheckoutResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default checkoutRoute;
