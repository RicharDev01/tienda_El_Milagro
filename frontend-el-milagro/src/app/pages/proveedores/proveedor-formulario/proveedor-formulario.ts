import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { Proveedor } from '../../../models/proveedor';
import { ProveedorService } from '../../../services/proveedor';

@Component({
  selector: 'app-proveedor-formulario',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './proveedor-formulario.html',
  styleUrl: './proveedor-formulario.scss'
})
export class ProveedorFormulario implements OnInit {

  formulario!: FormGroup;
  modo: 'crear' | 'editar' | 'ver';

  constructor(
    private fb: FormBuilder,
    private proveedorService: ProveedorService,
    private dialogRef: MatDialogRef<ProveedorFormulario>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      modo: 'crear' | 'editar' | 'ver',
      proveedor: Proveedor
    }
  ) {
    this.modo = data.modo;
  }

  ngOnInit(): void {
    this.formulario = this.fb.group({
      id: [''],
      nombreProveedor: ['', Validators.required],
      contacto: ['', Validators.required],
      esActivo: [true]
    });

    if (this.data.proveedor) {
      this.formulario.patchValue(this.data.proveedor);
    }

    if (this.modo === 'ver') {
      this.formulario.disable();
    }
  }

  guardar(): void {
    if (this.formulario.invalid) return;

    const proveedor = this.formulario.value;

    if (this.modo === 'crear') {
      this.proveedorService.registrarProveedor(proveedor).subscribe(() => {
        this.dialogRef.close('creado');
      });
    }

    if (this.modo === 'editar') {
      this.proveedorService.actualizarProveedor(proveedor.id, proveedor).subscribe(() => {
        this.dialogRef.close('editado');
      });
    }
  }

  cerrar(): void {
    this.dialogRef.close();
  }
}