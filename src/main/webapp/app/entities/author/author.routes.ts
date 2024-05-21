import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { AuthorComponent } from './list/author.component';
import { AuthorDetailComponent } from './detail/author-detail.component';
import { AuthorUpdateComponent } from './update/author-update.component';
import AuthorResolve from './route/author-routing-resolve.service';

const authorRoute: Routes = [
  {
    path: '',
    component: AuthorComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AuthorDetailComponent,
    resolve: {
      author: AuthorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AuthorUpdateComponent,
    resolve: {
      author: AuthorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AuthorUpdateComponent,
    resolve: {
      author: AuthorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default authorRoute;
