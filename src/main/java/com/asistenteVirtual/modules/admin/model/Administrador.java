package com.asistenteVirtual.modules.admin.model;

import com.asistenteVirtual.common.model.Persona;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "persona_id")
@SuperBuilder
public class Administrador extends Persona {
    // Sin campos extra por ahora, hereda de Persona
}