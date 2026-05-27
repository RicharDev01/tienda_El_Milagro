import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { ClienteService } from '../../services/cliente';
import { Cliente } from '../../models/cliente';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ClienteFormulario } from './cliente-formulario/cliente-formulario';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  templateUrl: './clientes.html',
  styleUrl: './clientes.scss'
})
export class ClientesComponent implements OnInit, AfterViewInit {

  columnas: string[] = ['dui', 'nombre', 'apellido', 'estado', 'acciones'];

  dataSource = new MatTableDataSource<Cliente>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private clienteService: ClienteService,
    private dialog: MatDialog, private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.obtenerClientes();

    this.dataSource.filterPredicate = (cliente: Cliente, filtro: string) => {
      const texto = `${cliente.dui} ${cliente.primerNombre} ${cliente.segundoNombre ?? ''} ${cliente.primerApellido} ${cliente.segundoApellido ?? ''}`.toLowerCase();
      return texto.includes(filtro);
    };

    this.dataSource.sortingDataAccessor = (cliente: Cliente, propiedad: string) => {
      switch (propiedad) {
        case 'nombre':
          return `${cliente.primerNombre} ${cliente.segundoNombre ?? ''}`.toLowerCase();

        case 'apellido':
          return `${cliente.primerApellido} ${cliente.segundoApellido ?? ''}`.toLowerCase();

        case 'dui':
          return cliente.dui;

        case 'estado':
          return cliente.esActivo ? 1 : 0;

        default:
          return '';
      }
    };
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  obtenerClientes(): void {
    this.clienteService.listarClientes().subscribe({
      next: (data) => {
        this.dataSource.data = data;
      },
      error: (error) => {
        console.error('Error al obtener clientes', error);
      }
    });
  }

  aplicarFiltro(event: Event): void {
    const valor = (event.target as HTMLInputElement).value;
    this.dataSource.filter = valor.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  agregarCliente(): void {
    const dialogRef = this.dialog.open(ClienteFormulario, {
      width: '650px',
      data: {
        modo: 'crear',
        cliente: null
      }
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === 'creado') {
        this.obtenerClientes();
        this.mostrarMensaje('Cliente agregado con éxito');
      }
    });
  }

  verCliente(cliente: Cliente): void {
    this.dialog.open(ClienteFormulario, {
      width: '650px',
      data: {
        modo: 'ver',
        cliente
      }
    });
  }

  editarCliente(cliente: Cliente): void {
    const dialogRef = this.dialog.open(ClienteFormulario, {
      width: '650px',
      data: {
        modo: 'editar',
        cliente
      }
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === 'editado') {
        this.obtenerClientes();
        this.mostrarMensaje('Cliente editado con éxito');
      }
    });
  }

  eliminarCliente(cliente: Cliente): void {
    const confirmar = confirm(
      `¿Desea eliminar al cliente ${cliente.primerNombre} ${cliente.primerApellido}?`
    );

    if (!confirmar) return;

    this.clienteService.eliminarCliente(cliente.dui).subscribe({
      next: () => {
        this.obtenerClientes();
        this.mostrarMensaje('Cliente eliminado con éxito');
      },
      error: (error) => {
        console.error('Error al eliminar cliente', error);
      }
    });
  }

  mostrarMensaje(mensaje: string): void {
    this.snackBar.open(mensaje, 'Cerrar', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: ['snackbar-exito']
    });
  }
}