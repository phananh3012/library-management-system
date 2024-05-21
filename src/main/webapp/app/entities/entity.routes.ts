import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'projectMiniApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'author',
    data: { pageTitle: 'projectMiniApp.author.home.title' },
    loadChildren: () => import('./author/author.routes'),
  },
  {
    path: 'book',
    data: { pageTitle: 'projectMiniApp.book.home.title' },
    loadChildren: () => import('./book/book.routes'),
  },
  {
    path: 'book-copy',
    data: { pageTitle: 'projectMiniApp.bookCopy.home.title' },
    loadChildren: () => import('./book-copy/book-copy.routes'),
  },
  {
    path: 'category',
    data: { pageTitle: 'projectMiniApp.category.home.title' },
    loadChildren: () => import('./category/category.routes'),
  },
  {
    path: 'checkout',
    data: { pageTitle: 'projectMiniApp.checkout.home.title' },
    loadChildren: () => import('./checkout/checkout.routes'),
  },
  {
    path: 'hold',
    data: { pageTitle: 'projectMiniApp.hold.home.title' },
    loadChildren: () => import('./hold/hold.routes'),
  },
  {
    path: 'notification',
    data: { pageTitle: 'projectMiniApp.notification.home.title' },
    loadChildren: () => import('./notification/notification.routes'),
  },
  {
    path: 'patron-account',
    data: { pageTitle: 'projectMiniApp.patronAccount.home.title' },
    loadChildren: () => import('./patron-account/patron-account.routes'),
  },
  {
    path: 'publisher',
    data: { pageTitle: 'projectMiniApp.publisher.home.title' },
    loadChildren: () => import('./publisher/publisher.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
