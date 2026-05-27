import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClienteFormulario } from './cliente-formulario';

describe('ClienteFormulario', () => {
  let component: ClienteFormulario;
  let fixture: ComponentFixture<ClienteFormulario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteFormulario]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClienteFormulario);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
