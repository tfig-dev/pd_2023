package fact_rest;

/**
 *
 * @author Jose Marinho
 */
public class Fact {

    private String fact;
    private int length;

    public Fact() {}

    public String getFact() {
        return fact;
    }

    public int getLength() {
        return length;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Fact {" +
            "fact=" + fact +
            ", length='" + length + '\'' +
            '}';
    }
}
