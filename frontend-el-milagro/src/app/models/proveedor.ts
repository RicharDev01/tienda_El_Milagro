export interface Proveedor {
  id?: number;
  nombreProveedor: string;
  contacto: string;
  esActivo: boolean;
  fechaRegistro?: string;
  fechaModificacion?: string;
}