import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private apiUrl = 'http://localhost:2100/clientes';

  constructor(private http: HttpClient) { }

  listarClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.apiUrl}/listar`);
  }

  registrarCliente(cliente: Cliente) {
    return this.http.post<Cliente>(`${this.apiUrl}/registrar`, cliente);
  }

  actualizarCliente(dui: string, cliente: Cliente) {
    return this.http.put<Cliente>(`${this.apiUrl}/actualizar/${dui}`, cliente);
  }

  eliminarCliente(dui: string) {
    return this.http.delete(`${this.apiUrl}/eliminar/${dui}`, {
      responseType: 'text'
    });
  }

}