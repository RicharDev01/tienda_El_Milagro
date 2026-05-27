import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

import { Proveedor } from '../../models/proveedor';
import { ProveedorService } from '../../services/proveedor';

import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProveedorFormulario } from './proveedor-formulario/proveedor-formulario';

@Component({
  selector: 'app-proveedores',
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
  templateUrl: './proveedores.html',
  styleUrl: './proveedores.scss'
})
export class ProveedoresComponent implements OnInit, AfterViewInit {

  columnas: string[] = ['id', 'nombreProveedor', 'contacto', 'estado', 'acciones'];

  dataSource = new MatTableDataSource<Proveedor>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private proveedorService: ProveedorService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.obtenerProveedores();

    this.dataSource.filterPredicate = (proveedor: Proveedor, filtro: string) => {
      const texto = `${proveedor.id} ${proveedor.nombreProveedor} ${proveedor.contacto}`.toLowerCase();
      return texto.includes(filtro);
    };

    this.dataSource.sortingDataAccessor = (proveedor: Proveedor, propiedad: string) => {
      switch (propiedad) {
        case 'id':
          return proveedor.id ?? 0;
        case 'nombreProveedor':
          return proveedor.nombreProveedor.toLowerCase();
        case 'contacto':
          return proveedor.contacto.toLowerCase();
        case 'estado':
          return proveedor.esActivo ? 1 : 0;
        default:
          return '';
      }
    };
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  obtenerProveedores(): void {
    this.proveedorService.listarProveedores().subscribe({
      next: (data) => {
        this.dataSource.data = data;
      },
      error: (error) => {
        console.error('Error al obtener proveedores', error);
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

  agregarProveedor(): void {
    const dialogRef = this.dialog.open(ProveedorFormulario, {
      width: '600px',
      data: {
        modo: 'crear',
        proveedor: null
      }
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === 'creado') {
        this.obtenerProveedores();
        this.mostrarMensaje('Proveedor agregado con éxito');
      }
    });
  }

  verProveedor(proveedor: Proveedor): void {
    this.dialog.open(ProveedorFormulario, {
      width: '600px',
      data: {
        modo: 'ver',
        proveedor
      }
    });
  }

  editarProveedor(proveedor: Proveedor): void {
    const dialogRef = this.dialog.open(ProveedorFormulario, {
      width: '600px',
      data: {
        modo: 'editar',
        proveedor
      }
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === 'editado') {
        this.obtenerProveedores();
        this.mostrarMensaje('Proveedor editado con éxito');
      }
    });
  }

  eliminarProveedor(proveedor: Proveedor): void {
    const confirmar = confirm(
      `¿Desea desactivar al proveedor ${proveedor.nombreProveedor}?`
    );

    if (!confirmar || proveedor.id == null) return;

    this.proveedorService.eliminarProveedor(proveedor.id).subscribe({
      next: () => {
        this.obtenerProveedores();
        this.mostrarMensaje('Proveedor eliminado con éxito');
      },
      error: (error) => {
        console.error('Error al eliminar proveedor', error);
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