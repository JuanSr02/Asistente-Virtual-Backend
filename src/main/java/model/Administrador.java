package model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "Administrador")
@PrimaryKeyJoinColumn(name = "Persona_id")
public class Administrador extends Persona {

    @Column(nullable = false)
    private Integer token;

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }
}


