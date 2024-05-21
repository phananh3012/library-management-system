import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { BookCopyComponent } from './list/book-copy.component';
import { BookCopyDetailComponent } from './detail/book-copy-detail.component';
import { BookCopyUpdateComponent } from './update/book-copy-update.component';
import BookCopyResolve from './route/book-copy-routing-resolve.service';

const bookCopyRoute: Routes = [
  {
    path: '',
    component: BookCopyComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BookCopyDetailComponent,
    resolve: {
      bookCopy: BookCopyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BookCopyUpdateComponent,
    resolve: {
      bookCopy: BookCopyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BookCopyUpdateComponent,
    resolve: {
      bookCopy: BookCopyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookCopyRoute;
