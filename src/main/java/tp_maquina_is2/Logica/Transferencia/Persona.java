/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tp_maquina_is2.Logica.Transferencia;

/**
 *
 * @author ivanb
 */
public abstract class Persona {
    private String codigo;
    private String nombre;
    private String apellido;
    private int dni;

    public Persona(String codigo, String nombre, String apellido, int dni) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    @Override
    public String toString() {
        return "Nombre y Apellido: " + nombre + " " + apellido + " DNI: " + dni;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object.getClass() != this.getClass()) {
            return false;
        }

        return codigo.equals(((Persona)object).getCodigo())
                && nombre.equals(((Persona)object).getNombre())
                && apellido.equals(((Persona)object).getApellido())
                && dni == ((Persona)object).getDni();
    }
    
}
