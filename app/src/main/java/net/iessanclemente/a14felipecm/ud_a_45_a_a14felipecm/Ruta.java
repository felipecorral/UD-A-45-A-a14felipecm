package net.iessanclemente.a14felipecm.ud_a_45_a_a14felipecm;

/**
 * Created by felipe on 28/11/15.
 */
public class Ruta {

    private String nome;
    private String descripcion;

    public Ruta(String nome, String descripcion) {
        this.nome = nome;
        this.descripcion = descripcion;
    }

    public Ruta(){

    }

    public String getNome() {
        return nome;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "nome='" + nome + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
