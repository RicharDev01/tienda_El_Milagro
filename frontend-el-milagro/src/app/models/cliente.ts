export interface Cliente {
  dui: string;
  idInterno?: number;
  primerNombre: string;
  segundoNombre?: string;
  primerApellido: string;
  segundoApellido?: string;
  fechaNacimiento?: string;
  esActivo: boolean;
  fechaRegistro?: string;
  fechaModificacion?: string;
}