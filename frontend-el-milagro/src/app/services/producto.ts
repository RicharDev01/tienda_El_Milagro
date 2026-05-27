import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  private apiUrl = 'http://localhost:2100/productos';

  constructor(private http: HttpClient) {}

  listarProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/listar`);
  }

  registrarProducto(producto: Producto) {
    return this.http.post<Producto>(`${this.apiUrl}/registrar`, producto);
  }

  actualizarProducto(id: number, producto: Producto) {
    return this.http.put<Producto>(`${this.apiUrl}/actualizar/${id}`, producto);
  }

  eliminarProducto(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/eliminar/${id}`);
  }
}