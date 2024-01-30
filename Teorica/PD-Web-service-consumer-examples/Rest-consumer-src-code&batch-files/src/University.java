package universities_list_rest;

/**
 *
 * @author Jose Marinho
 */
public class University {
    private String country;
    private String [] domains;
    private String [] web_pages;
    private String alpha_two_code;
    private String name;
    private String state_province;

    public University() {}

    public String getCountry(){
        return country;
    }

    public String [] getDomains(){
        return domains;
    }

    public String [] getWebPages(){
        return web_pages;
    }

    public String getAlphaTwoCode(){
        return alpha_two_code;
    }

    public String getName(){
        return name;
    }

    public String getStateProvince(){
        return state_province;
    }

    public String toString(){
        return name + " (" + country + ")";
    }
}