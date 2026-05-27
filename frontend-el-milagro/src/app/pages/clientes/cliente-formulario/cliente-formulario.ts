import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef
} from '@angular/material/dialog';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { Cliente } from '../../../models/cliente';
import { ClienteService } from '../../../services/cliente';

@Component({
  selector: 'app-cliente-formulario',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './cliente-formulario.html',
  styleUrl: './cliente-formulario.scss'
})
export class ClienteFormulario implements OnInit {

  formulario!: FormGroup;

  modo: 'crear' | 'editar' | 'ver';

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private dialogRef: MatDialogRef<ClienteFormulario>,

    @Inject(MAT_DIALOG_DATA)
    public data: {
      modo: 'crear' | 'editar' | 'ver',
      cliente: Cliente
    }
  ) {
    this.modo = data.modo;
  }

  ngOnInit(): void {

    this.formulario = this.fb.group({
      dui: ['', Validators.required],
      primerNombre: ['', Validators.required],
      segundoNombre: [''],
      primerApellido: ['', Validators.required],
      segundoApellido: [''],
      fechaNacimiento: ['']
    });

    if (this.data.cliente) {
      this.formulario.patchValue(this.data.cliente);
    }

    if (this.modo === 'ver') {
      this.formulario.disable();
    }

  }

  guardar(): void {

    if (this.formulario.invalid) return;

    const cliente = this.formulario.value;

    if (this.modo === 'crear') {

      this.clienteService.registrarCliente(cliente)
        .subscribe(() => {
          this.dialogRef.close('creado');
        });

    } else if (this.modo === 'editar') {

      this.clienteService.actualizarCliente(cliente.dui, cliente)
        .subscribe(() => {
          this.dialogRef.close('editado');
        });
    }
  }

  cerrar(): void {
    this.dialogRef.close();
  }
}