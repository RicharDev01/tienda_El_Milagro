import { Proveedor } from './proveedor';

export interface Producto {
  id?: number;
  proveedor: Proveedor;
  nombreProducto: string;
  precioProducto: number;
  esActivo: boolean;
  fechaRegistro?: string;
  fechaModificacion?: string;
}