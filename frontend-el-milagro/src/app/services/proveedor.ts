import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Proveedor } from '../models/proveedor';

@Injectable({
  providedIn: 'root'
})
export class ProveedorService {

  private apiUrl = 'http://localhost:2100/proveedores';

  constructor(private http: HttpClient) {}

  listarProveedores(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(`${this.apiUrl}/listar`);
  }

  registrarProveedor(proveedor: Proveedor) {
    return this.http.post<Proveedor>(`${this.apiUrl}/registrar`, proveedor);
  }

  actualizarProveedor(id: number, proveedor: Proveedor) {
    return this.http.put<Proveedor>(`${this.apiUrl}/actualizar/${id}`, proveedor);
  }

  eliminarProveedor(id: number) {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`);
  }

  reactivarProveedor(id: number) {
    return this.http.patch<Proveedor>(`${this.apiUrl}/reactivar/${id}`, {});
  }

  buscarPorNombre(valor: string): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(`${this.apiUrl}/buscar/nombre?valor=${valor}`);
  }
}