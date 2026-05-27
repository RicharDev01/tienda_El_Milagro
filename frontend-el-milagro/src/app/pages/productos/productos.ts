import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

import { Producto } from '../../models/producto';
import { ProductoService } from '../../services/producto';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProductoFormulario } from './producto-formulario/producto-formulario';

@Component({
  selector: 'app-productos',
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
  templateUrl: './productos.html',
  styleUrl: './productos.scss'
})
export class ProductosComponent implements OnInit, AfterViewInit {

  columnas: string[] = ['id', 'nombreProducto', 'proveedor', 'precioProducto', 'estado', 'acciones'];

  dataSource = new MatTableDataSource<Producto>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private productoService: ProductoService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.obtenerProductos();

    this.dataSource.filterPredicate = (producto: Producto, filtro: string) => {
      const texto = `
        ${producto.id}
        ${producto.nombreProducto}
        ${producto.proveedor?.nombreProveedor ?? ''}
        ${producto.precioProducto}
      `.toLowerCase();

      return texto.includes(filtro);
    };

    this.dataSource.sortingDataAccessor = (producto: Producto, propiedad: string) => {
      switch (propiedad) {
        case 'id':
          return producto.id ?? 0;
        case 'nombreProducto':
          return producto.nombreProducto?.toLowerCase() ?? '';
        case 'proveedor':
          return producto.proveedor?.nombreProveedor?.toLowerCase() ?? '';
        case 'precioProducto':
          return producto.precioProducto ?? 0;
        case 'estado':
          return producto.esActivo ? 1 : 0;
        default:
          return '';
      }
    };
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  obtenerProductos(): void {
    this.productoService.listarProductos().subscribe({
      next: (data) => {
        this.dataSource.data = data;
      },
      error: (error) => {
        console.error('Error al obtener productos', error);
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

  agregarProducto(): void {
    const dialogRef = this.dialog.open(ProductoFormulario, {
      width: '700px',
      data: {
        modo: 'crear',
        producto: null
      }
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === 'creado') {
        this.obtenerProductos();
        this.mostrarMensaje('Producto agregado con éxito');
      }
    });
  }

  verProducto(producto: Producto): void {
    this.dialog.open(ProductoFormulario, {
      width: '700px',
      data: {
        modo: 'ver',
        producto
      }
    });
  }

  editarProducto(producto: Producto): void {
    const dialogRef = this.dialog.open(ProductoFormulario, {
      width: '700px',
      data: {
        modo: 'editar',
        producto
      }
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === 'editado') {
        this.obtenerProductos();
        this.mostrarMensaje('Producto editado con éxito');
      }
    });
  }

  eliminarProducto(producto: Producto): void {
    const confirmar = confirm(
      `¿Desea eliminar el producto ${producto.nombreProducto}?`
    );

    if (!confirmar || producto.id == null) return;

    this.productoService.eliminarProducto(producto.id).subscribe({
      next: () => {
        this.obtenerProductos();
        this.mostrarMensaje('Producto eliminado con éxito');
      },
      error: (error) => {
        console.error('Error al eliminar producto', error);
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