package pt.isec.pd.eventsManager.api;

import com.nimbusds.jose.util.Base64;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.Scanner;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import pt.isec.pd.eventsManager.api.repository.Data;

public class UserTerminal {

    public static String sendRequestAndShowResponse(String uri, String verb, String authorizationValue, String body) throws MalformedURLException, IOException {
        String responseBody = null;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(verb);
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

            String tokenUser = sendRequestAndShowResponse(login, "GET", "basic " + encodedString, null);
            System.out.println("Token do user: " + tokenUser);

            String tokenAttendances = sendRequestAndShowResponse(attendances,"GET", "bearer " + tokenUser, null);

            String tokenAttendancesNAME = sendRequestAndShowResponse(attendances + "?name=leitoes","GET", "bearer " + tokenUser, null);

            String tokenAttendancesLOCAL = sendRequestAndShowResponse(attendances + "?location=ISEC","GET", "bearer " + tokenUser, null);

            String tokenAttendancesLOCAL2 = sendRequestAndShowResponse(attendances + "?location=ESEC","GET", "bearer " + tokenUser, null);

            String tokenAttendancesDATA = sendRequestAndShowResponse(attendances + "?startDate=2023-11-10&endDate=2023-11-21","GET", "bearer " + tokenUser, null);

            String insertCode = "{\"code\":\"AXA435\"}";
            String tokenCodigoAttendances = sendRequestAndShowResponse(attendances,"POST", "bearer " + tokenUser, insertCode);

            String insertCode2 = "{\"code\":\"n5DMy\"}";
            String tokenCodigoAttendances2 = sendRequestAndShowResponse(attendances,"POST", "bearer " + tokenUser, insertCode2);

        } else if (user == 2) {
            System.out.println("login admin:\n");

            String hashedKey = Data.generateBase64("admin", "admin");

            //String token = Data.sendRequestAndShowResponse( login,"GET", "basic " + hashedKey, null);

            Map<String, String> responseMap = Data.sendLoginRequest(login,"GET", "basic " + hashedKey, null);

            System.out.println(responseMap.get("admin"));

            String token = responseMap.get("token");
            System.out.println("Token do user: " + token);



//                String encodedString_Admin = String.valueOf(Base64.encode("admin:admin"));
//            System.out.println("Base64: " + encodedString_Admin + "\n" + "Info: admin:admin");

            //String token = sendRequestAndShowResponse(login, "GET", "basic " + encodedString_Admin, null);
            //System.out.println("Token do user: " + token);

            String token2 = sendRequestAndShowResponse(events, "GET", "bearer " + token, null);

            String token3 = sendRequestAndShowResponse(events + "/2", "GET", "bearer " + token, null);

            String getRecord = sendRequestAndShowResponse(attendances + "/1", "GET", "bearer " + token, null);

            String newEvent = "{\"name\":\"Tiago dos leitoes 2\",\"location\":\"ISEC\",\"date\":\"2023-11-19\",\"startTime\":\"18:30\",\"endTime\":\"19:30\"}";
            String tokenEvent = sendRequestAndShowResponse(events, "POST", "bearer " + token, newEvent);

            String token4 = sendRequestAndShowResponse(eventsExtra, "GET", "bearer " + token, null);

            String token5 = sendRequestAndShowResponse(eventsISEC, "GET", "bearer " + token, null);

            String token6 = sendRequestAndShowResponse(eventsESEC, "GET", "bearer " + token, null);

            String token7 = sendRequestAndShowResponse(eventsISEC_M, "GET", "bearer " + token, null);

            String token8 = sendRequestAndShowResponse(events + "?startDate=2023-11-10&endDate=2023-11-21", "GET", "bearer " + token, null);

            String tokenDelete = sendRequestAndShowResponse(events + "/2", "DELETE", "bearer " + token, null);

            String tokenDelete9 = sendRequestAndShowResponse(events + "/9", "DELETE", "bearer " + token, null);

            String tokenDelete10 = sendRequestAndShowResponse(events + "/10", "DELETE", "bearer " + token, null);

            String newCODE = "{\"timeout\":30}";
            String tokenGENERATECode = sendRequestAndShowResponse(events + "/1", "PUT", "bearer " + token, newCODE);

        } else if (user == 3) {
            System.out.println("register:\n");
            String jsonInputString_2 =
                    "{\"username\":\"Ze Antonio\"," +
                            "\"email\":\"tigas3@gmail.com\"," +
                            "\"password\":\"admin\"," +
                            "\"nif\":1234567" + "}";

            String tknRegister = sendRequestAndShowResponse(register, "POST", null, jsonInputString_2);

            System.out.println("login novo user:\n");
            String encodedString = String.valueOf(Base64.encode("tigas@gmail.com:admin"));
            System.out.println("Base64: " + encodedString + "\n" + "Info: tigas@gmail.com:admin");

            String token = sendRequestAndShowResponse(login, "GET", "basic " + encodedString, null);
            System.out.println("Token do user: " + token);

        }
    }
}
