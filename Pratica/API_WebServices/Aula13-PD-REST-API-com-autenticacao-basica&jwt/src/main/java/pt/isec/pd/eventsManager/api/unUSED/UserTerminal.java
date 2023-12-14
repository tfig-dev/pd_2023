package pt.isec.pd.eventsManager.api.unUSED;

import com.nimbusds.jose.util.Base64;
import pt.isec.pd.eventsManager.api.models.Event;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class UserTerminal {

    public static String sendRequestAndShowResponse_v2(String uri, String verb, String authorizationValue, String body) throws MalformedURLException, IOException {
        String responseBody = null;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(verb);
        //connection.setRequestProperty("Content-Type", "Application/Json");
        connection.setRequestProperty("Accept", "application/xml, */*");

        if(authorizationValue!=null) {
            connection.setRequestProperty("Authorization", authorizationValue);
        }

        if(body!=null){
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "Application/Json");
            connection.getOutputStream().write(body.getBytes());
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " +  responseCode + " (" + connection.getResponseMessage() + ")");

        Scanner s;

        if(connection.getErrorStream()!=null) {
            s = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        }

        try {
            s = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        } catch (IOException e){}

        connection.disconnect();

        System.out.println(verb + " " + uri + (body==null?"":" with body: "+body) + " ==> " + responseBody);
        System.out.println();

        return responseBody;
    }

    public static void main(String args[]) throws IOException, URISyntaxException {
        //se for o erro 415 temos que adicionar a property do json
        //con.setRequestProperty("Content-Type", "application/json");

        URI helloUri = new URI("http://localhost:8080/hello/fr?name=Jeanne");
        URI helloUri2 = new URI("http://localhost:8080/hello/gr?name=Jeanne");
        URI loginUri = new URI("http://localhost:8080/login");
        URI eventsURI = new URI("http://localhost:8080/events");

        URI loremUri = new URI("http://localhost:8080/lorem?type=paragraph");

        URI trial = new URI("http://localhost:8080");

        String login = "http://localhost:8080/login";
        String events = "http://localhost:8080/events";
        String eventsExtra = "http://localhost:8080/events?name=Tiago";
        String eventsISEC = "http://localhost:8080/events?location=ISEC";
        String eventsESEC = "http://localhost:8080/events?location=ESEC";
        String eventsISEC_M = "http://localhost:8080/events?location=isec";
        String register = "http://localhost:8080/register";
        String attendances = "http://localhost:8080/attendances";

        Scanner scanner = new Scanner(System.in);

        System.out.println("1 - USER\n2 - ADMIN\n3 - REGISTER");

        int user = scanner.nextInt();

        System.out.println(user);

        if (user == 1) {
            System.out.println("login user:\n");
            String encodedString = String.valueOf(Base64.encode("tiagofr.figueiredo@gmail.com:admin"));
            System.out.println("Base64: " + encodedString + "\n" + "Info: tiagofr.figueiredo@gmail.com:admin");

            String tokenUser = sendRequestAndShowResponse_v2(login, "GET", "basic " + encodedString, null);
            System.out.println("Token do user: " + tokenUser);

            String tokenAttendances = sendRequestAndShowResponse_v2(attendances,"GET", "bearer " + tokenUser, null);
            String tokenAttendancesNAME = sendRequestAndShowResponse_v2(attendances + "?name=leitoes","GET", "bearer " + tokenUser, null);

            String tokenAttendancesLOCAL = sendRequestAndShowResponse_v2(attendances + "?location=ISEC","GET", "bearer " + tokenUser, null);

            String tokenAttendancesLOCAL2 = sendRequestAndShowResponse_v2(attendances + "?location=ESEC","GET", "bearer " + tokenUser, null);

            String tokenAttendancesDATA = sendRequestAndShowResponse_v2(attendances + "?startDate=2023-11-10&endDate=2023-11-21","GET", "bearer " + tokenUser, null);

            String insertCode = "{\"code\":\"AXA435\"}";
            String tokenCodigoAttendances = sendRequestAndShowResponse_v2(attendances,"POST", "bearer " + tokenUser, insertCode);

        } else if (user == 2) {
            System.out.println("login admin:\n");
            String encodedString_Admin = String.valueOf(Base64.encode("admin:admin"));
            System.out.println("Base64: " + encodedString_Admin + "\n" + "Info: admin:admin");

            String token = sendRequestAndShowResponse_v2(login, "GET", "basic " + encodedString_Admin, null);
            System.out.println("Token do user: " + token);

            String token2 = sendRequestAndShowResponse_v2(events, "GET", "bearer " + token, null);

            String token3 = sendRequestAndShowResponse_v2(events + "/2", "GET", "bearer " + token, null);

            String getRecord = sendRequestAndShowResponse_v2(attendances + "/1", "GET", "bearer " + token, null);

            String newEvent = "{\"ID\":1,\"name\":\"Tiago dos leitoes 2\",\"location\":\"ISEC\",\"date\":\"2023-11-19\",\"startTime\":\"18:30\",\"endTime\":\"19:30\"}";
            String tokenEvent = sendRequestAndShowResponse_v2(events, "POST", "bearer " + token, newEvent);

            String token4 = sendRequestAndShowResponse_v2(eventsExtra, "GET", "bearer " + token, null);

            String token5 = sendRequestAndShowResponse_v2(eventsISEC, "GET", "bearer " + token, null);

            String token6 = sendRequestAndShowResponse_v2(eventsESEC, "GET", "bearer " + token, null);

            String token7 = sendRequestAndShowResponse_v2(eventsISEC_M, "GET", "bearer " + token, null);

            //
            String token8 = sendRequestAndShowResponse_v2(events + "?startDate=2023-11-10&endDate=2023-11-21", "GET", "bearer " + token, null);

            String tokenDelete = sendRequestAndShowResponse_v2(events + "/2", "DELETE", "bearer " + token, null);

            String tokenDelete9 = sendRequestAndShowResponse_v2(events + "/9", "DELETE", "bearer " + token, null);

            String tokenDelete10 = sendRequestAndShowResponse_v2(events + "/10", "DELETE", "bearer " + token, null);

        } else if (user == 3) {
            System.out.println("register:\n");
            String jsonInputString_2 =
                    "{\"username\":\"Ze Antonio\"," +
                            "\"email\":\"tigas3@gmail.com\"," +
                            "\"password\":\"admin\"," +
                            "\"nif\":1234567" + "}";

            String tknRegister = sendRequestAndShowResponse_v2(register, "POST", null, jsonInputString_2);

            System.out.println("login novo user:\n");
            String encodedString = String.valueOf(Base64.encode("tigas@gmail.com:admin"));
            System.out.println("Base64: " + encodedString + "\n" + "Info: tigas@gmail.com:admin");

            String token = sendRequestAndShowResponse_v2(login, "GET", "basic " + encodedString, null);
            System.out.println("Token do user: " + token);

        }


        //GET com parametros
        //OK
        //sendRequestAndShowResponse(loremUri+"?type=word&length=6", "GET", "bearer " + token, null);

        //PUT não suportado para esta URI
        //sendRequestAndShowResponse(loremUri, "PUT", "bearer " + token, null);

        //POST sem corpo de mensagem
        //sendRequestAndShowResponse(loremUri, "POST", "bearer " + token, null);

        //Ok
        //sendRequestAndShowResponse(loremUri, "POST", "bearer " + token, "{\"type\":\"word\",\"length\":4}");





//        System.out.println(sendReq(new URI(trial.toString() + register), "POST", null));




        //PROFESSOR
//        String helloUri = "http://localhost:8080/hello/fr?name=Jeanne";
//        String helloUri2 = "http://localhost:8080/hello/gr?name=Jeanne";
//        String loginUri = "http://localhost:8080/login";
//        String loremUri = "http://localhost:8080/lorem";
//
//        System.out.println();
//
//        //OK
//        sendRequestAndShowResponse(helloUri, "GET", null, null);
//
//        //Língua "gr" não suportada
//        sendRequestAndShowResponse(helloUri2, "GET", null, null);
//
//        //Falta um campo "Authorization: basic ..." válido no cabeçalho do pedido para autenticação básica
//        String token = sendRequestAndShowResponse(loginUri, "POST",null, null);
//
//        //OK
//        String credentials = java.util.Base64.getEncoder().encodeToString("admin:admin".getBytes());
//        token = sendRequestAndShowResponse(loginUri, "POST","basic "+ credentials, null); //Base64(admin:admin) YWRtaW46YWRtaW4=
//
//        //Falta um campo "Authorization: bearer ..." no cabeçalho do pedido com um token JWT válido
//        sendRequestAndShowResponse(loremUri, "GET", null, null);
//
//        //OK
//        sendRequestAndShowResponse(loremUri+"?type=word&length=6", "GET", "bearer " + token, null);
//
//        //PUT não suportado para esta URI
//        sendRequestAndShowResponse(loremUri, "PUT", "bearer " + token, null);
//
//        //POST sem corpo de mensagem
//        sendRequestAndShowResponse(loremUri, "POST", "bearer " + token, null);
//
//        //Ok
//        sendRequestAndShowResponse(loremUri, "POST", "bearer " + token, "{\"type\":\"word\",\"length\":4}");



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
