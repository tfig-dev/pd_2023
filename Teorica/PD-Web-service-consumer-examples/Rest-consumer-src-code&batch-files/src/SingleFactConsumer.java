package fact_rest;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.json.*;

import java.util.Set;
import java.util.Map;

/**
 *
 * @author José Marinho
 */
public class SingleFactConsumer {
    
    public static void main(String args[]) throws MalformedURLException, IOException {
        getRandomFact();
    }
    
    public static void getRandomFact() throws MalformedURLException, IOException
    {
        String uri = "https://catfact.ninja/fact";
        
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml, */*");
               
        InputStream jsonStream = connection.getInputStream();
        
        JsonReader jsonReader = Json.createReader(jsonStream);
        JsonObject object = jsonReader.readObject();
        
        jsonReader.close();
        connection.disconnect();
        
        /*
        System.out.println("(JsonObject) object.toString() -> " + object);
        System.out.println();
        System.out.println("-> \"JsonObject\" é um mapa:");
        System.out.println();
        
        System.out.println("  - object.keySet() -> "+object.keySet());
        System.out.println("  - object.get(\"fact\") -> "+object.get("fact"));
        System.out.println("  - object.get(\"length\") -> "+object.get("length"));
        System.out.println();
        System.out.println("  - object.values() -> "+object.values());
        System.out.println();

        Iterator it = object.values().iterator();
        while(it.hasNext()){
            System.out.println("  - " + it.next());
        }        
        System.out.println();
        */

        Gson gson = new GsonBuilder().create();               
        Fact fact = gson.fromJson(object.toString(), Fact.class);

        System.out.println();
        System.out.println("Fact fact = gson.fromJson(object.toString(), Fact.class): ");
        System.out.println();
        System.out.println("  - fact.toString() -> " + fact);
        System.out.println();
        System.out.println("  - fact.getFact() -> " + fact.getFact());
        System.out.println();
        System.out.println("  - fact.getLength() -> " + fact.getLength());
    }
    
}
