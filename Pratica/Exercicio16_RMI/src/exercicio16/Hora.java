package exercicio16;

import java.io.Serializable;

public class Hora implements Serializable {

    private static final long serialVersionUID = 1L;
    protected int horas, minutos, segundos;

    public Hora(int horas, int minutos, int segundos){
        this.horas = horas;
        this.minutos = minutos;
        this.segundos = segundos;
    }

    public int getHoras() { return this.horas; }
    public int getMinutos() { return this.minutos; }
    public int getSegundos() { return this.segundos;}

    public String toString() {
        return this.horas + ":" + this.minutos + "::" + this.segundos;
    }
}