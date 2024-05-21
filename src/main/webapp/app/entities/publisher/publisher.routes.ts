import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PublisherComponent } from './list/publisher.component';
import { PublisherDetailComponent } from './detail/publisher-detail.component';
import { PublisherUpdateComponent } from './update/publisher-update.component';
import PublisherResolve from './route/publisher-routing-resolve.service';

const publisherRoute: Routes = [
  {
    path: '',
    component: PublisherComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PublisherDetailComponent,
    resolve: {
      publisher: PublisherResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PublisherUpdateComponent,
    resolve: {
      publisher: PublisherResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PublisherUpdateComponent,
    resolve: {
      publisher: PublisherResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default publisherRoute;
