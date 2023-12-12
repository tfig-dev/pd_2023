package pt.isec.pd.eventsManager.api.unUSED;

import com.nimbusds.jose.util.Base64;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UserTerminal {

    public static String sendRequestAndShowResponse(URI uri, String verb, String authorizationValue) throws IOException {
        String responseBody = null;
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

        connection.setRequestMethod(verb);
        connection.setRequestProperty("Accept", "application/xml, */*");

        if (authorizationValue != null) {
            connection.setRequestProperty("Authorization", authorizationValue);
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode + " (" + connection.getResponseMessage() + ")");

        Scanner s;

        if (connection.getErrorStream() != null) {
            s = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        }

        try {
            s = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
        }

        connection.disconnect();

        System.out.println(verb + " " + uri + " -> " + responseBody);
        System.out.println();

        return responseBody;
    }

    public static String sendReq(URI uri, String verb, String authorizationValue) throws IOException {

        String jsonInputString =
                "{\"username\": \"Ze To\"," +
                        "\"nif\": 123456789," +
                        "\"email\": \"tiagofr.figueiredo@gmail.com\"," +
                        "\"password\": \"admin\"}";

        String jsonInputString_2 =
                "{\"username\": \"Ze Tolo\"," +
                        "\"nif\": 1234567," +
                        "\"email\": \"tiago@gmail.com\"," +
                        "\"password\": \"admin\"}";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return httpResponse.body() + " " + httpResponse.statusCode();
    }

    public static void main(String args[]) throws IOException, URISyntaxException {
        //se for o erro 415 temos que adicionar a property do json
        //con.setRequestProperty("Content-Type", "application/json");

        URI helloUri = new URI("http://localhost:8080/hello/fr?name=Jeanne");
        URI helloUri2 = new URI("http://localhost:8080/hello/gr?name=Jeanne");
        URI loginUri = new URI("http://localhost:8080/login");
        URI loremUri = new URI("http://localhost:8080/lorem?type=paragraph");

        URI trial = new URI("http://localhost:8080");
        String register = "/register";

        System.out.println("login:\n");
        String encodedString = String.valueOf(Base64.encode("tiagofr.figueiredo@gmail.com:admin"));
        System.out.println("Base64: " + encodedString + "\n" + "Info: tiagofr.figueiredo@gmail.com:admin");

        String token = sendRequestAndShowResponse(loginUri, "POST", "basic " + encodedString); //Base64(admin:admin)

        System.out.println("Token do user Tiago: " + token);

        System.out.println("register:\n");

        System.out.println(sendReq(new URI(trial.toString() + register), "POST", null));
        //sendRequestAndShowResponse(new URI(trial.toString() + login + loginTiago), "POST", null);

//        //OK
//        sendRequestAndShowResponse(helloUri, "GET", null);
//
//        //Língua "gr" não suportada
//        sendRequestAndShowResponse(helloUri2, "GET", null);
//
//        //Falta um campo "Authorization: basic ..." válido no cabeçalho do pedido para autenticação básica
//        String token = sendRequestAndShowResponse(loginUri, "POST", null);
//
//        //OK
//        token = sendRequestAndShowResponse(loginUri, "POST", "basic YWRtaW46YWRtaW4="); //Base64(admin:admin)
//
//        //Falta um campo "Authorization: bearer ..." no cabeçalho do pedido com um token JWT válido
//        sendRequestAndShowResponse(loremUri, "GET", null);
//
//        //OK
//        sendRequestAndShowResponse(loremUri, "GET", "bearer " + token);
//
//        //POST não suportado para esta URI
//        sendRequestAndShowResponse(loremUri, "POST", "bearer " + token);

    }
}
