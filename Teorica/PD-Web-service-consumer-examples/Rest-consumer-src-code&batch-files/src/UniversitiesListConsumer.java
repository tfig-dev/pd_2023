package universities_list_rest;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.json.*;

/**
 *
 * @author Jose Marinho
 */
public class UniversitiesListConsumer {
    
    public static void main(String args[]) throws MalformedURLException, IOException {

        if(args.length < 0){
            System.out.println("Deve indicar o nome do pais como primeiro argumento na linha de comandos!");
            System.exit(1);
        }

        String uri = "http://universities.hipolabs.com/search?country=" + args[0];
        
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml, */*");
        InputStream jsonStream = connection.getInputStream();
        
        JsonReader jsonReader = Json.createReader(jsonStream);
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
        connection.disconnect();

        Gson gson = new GsonBuilder().create();

        System.out.println();
        
        for(int i=0; i<array.size(); i++){
            JsonObject object = array.getJsonObject(i);
            //System.out.println(object);
            University university = gson.fromJson(object.toString(), University.class);
            System.out.println("\t- " + university);
        }
    }
    
}
