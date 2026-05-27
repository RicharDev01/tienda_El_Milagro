import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { AdminLayout } from './layouts/admin-layout/admin-layout';
import { Dashboard } from './pages/dashboard/dashboard';
import { ProductosComponent } from './pages/productos/productos';
import { ClientesComponent } from './pages/clientes/clientes';
import { Facturacion } from './pages/facturacion/facturacion';
import { Reportes } from './pages/reportes/reportes';
import { ProveedoresComponent } from './pages/proveedores/proveedores';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },

  {
    path: '',
    component: AdminLayout,
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'productos', component: ProductosComponent },
      { path: 'clientes', component: ClientesComponent },
      { path: 'facturacion', component: Facturacion },
      { path: 'reportes', component: Reportes },
      { path: 'proveedores', component: ProveedoresComponent}
    ]
  }
];