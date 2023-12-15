package pt.isec.pd.eventsManager.api.repository;

import io.jsonwebtoken.Jwts;
import com.nimbusds.jose.util.Base64;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import pt.isec.pd.eventsManager.api.models.Event;
import pt.isec.pd.eventsManager.api.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Data {
    private Connection connection;
    private static Data instance;
    public boolean isAdmin;
    public Jwt userDetails;
    public String tokenValue;

    public static Data getInstance(String location) {
        if (instance == null) {
            instance = new Data(location);
        }
        return instance;
    }

    public static Data getInstance() {
        if (instance == null) {
            throw new RuntimeException("Data instance not initialized");
        }
        return instance;
    }

    private Data(String location) {
        connect(location);
        createTables();
    }

    private void connect(String location){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String url = "jdbc:sqlite:" + location;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables() {
        Statement stmt;

        try {
            Class.forName("org.sqlite.JDBC");
            stmt = connection.createStatement();

            String users = "CREATE TABLE IF NOT EXISTS USER " +
                    "(EMAIL TEXT PRIMARY KEY," +
                    " NAME TEXT NOT NULL, " +
                    " PASSWORD TEXT NOT NULL, " +
                    " NIF CHAR(9) NOT NULL, " +
                    " ISADMIN INT NOT NULL)";
            stmt.executeUpdate(users);

            String events = "CREATE TABLE IF NOT EXISTS EVENT " +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " NAME TEXT NOT NULL," +
                    " LOCAL TEXT NOT NULL, " +
                    " DATE TEXT NOT NULL, " +
                    " BEGINHOUR TEXT NOT NULL, " +
                    " ENDHOUR TEXT NOT NULL," +
                    " CODE TEXT," +
                    " CODEEXPIRATIONTIME TEXT)";
            stmt.executeUpdate(events);

            String eventParticipants = "CREATE TABLE IF NOT EXISTS EVENT_PARTICIPANT " +
                    "(EVENT_ID INT," +
                    " USER_EMAIL STRING," +
                    " PRIMARY KEY (EVENT_ID, USER_EMAIL)," +
                    " FOREIGN KEY (EVENT_ID) REFERENCES EVENT(ID)," +
                    " FOREIGN KEY (USER_EMAIL) REFERENCES USER(EMAIL))";

            stmt.executeUpdate(eventParticipants);

            String createTable = "CREATE TABLE IF NOT EXISTS VERSION " +
                    "(VERSION INT NOT NULL DEFAULT 0)";
            stmt.executeUpdate(createTable);

            String insertRow = "INSERT INTO VERSION (VERSION) SELECT 0 WHERE NOT EXISTS (SELECT 1 FROM VERSION)";
            stmt.executeUpdate(insertRow);

            String insertAdmin = "INSERT OR IGNORE INTO USER (EMAIL, NAME, PASSWORD, NIF, ISADMIN) VALUES " +
                    "('admin', 'admin', 'admin', 123456789, 1)";
            stmt.executeUpdate(insertAdmin);


            String insertDefaultEvents = "INSERT OR IGNORE INTO EVENT (ID, NAME, LOCAL, DATE, BEGINHOUR, ENDHOUR) VALUES " +
                    "(1, 'Ze dos leitoes', 'ISEC', '2023-11-19', '15:30', '19:30'), " +
                    "(2, 'Maria das couves', 'ESEC', '2023-11-20', '16:30', '20:30')";
            stmt.executeUpdate(insertDefaultEvents);

            String insertDefaultUsers = "INSERT OR IGNORE INTO USER (EMAIL, NAME, PASSWORD, NIF, ISADMIN) VALUES " +
                    "('admin', 'admin', 'admin', 123456789, 1), " +
                    "('user@isec.pt', 'user', 'user', 987654321, 0), " +
                    "('user2@isec.pt', 'user2', 'user2', 987654321, 0), " +
                    "('user3@isec.pt', 'user3', 'user3', 123333333, 0)";
            stmt.executeUpdate(insertDefaultUsers);

            String insertDefaultParticipations = "INSERT OR IGNORE INTO EVENT_PARTICIPANT (EVENT_ID, USER_EMAIL) VALUES " +
                    "(1, 'user@isec.pt'), " +
                    "(2, 'user@isec.pt'), " +
                    "(1, 'user2@isec.pt')";
            stmt.executeUpdate(insertDefaultParticipations);


            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

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
        //System.out.println("Response code: " +  responseCode + " (" + connection.getResponseMessage() + ")");

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

        //System.out.println(verb + " " + uri + (body==null?"":" with body: "+body) + " ==> " + responseBody);

        System.out.println(responseBody);

        return responseBody;
    }

    public static Map<String, String> sendLoginRequest(String uri, String verb, String authorizationValue, String body) throws MalformedURLException, IOException {
        String responseBody = null;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(verb);
        connection.setRequestProperty("Accept", "application/xml, */*");

        if (authorizationValue != null) {
            connection.setRequestProperty("Authorization", authorizationValue);
        }

        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "Application/Json");
            connection.getOutputStream().write(body.getBytes());
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        //System.out.println("Response code: " + responseCode + " (" + connection.getResponseMessage() + ")");

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

        //System.out.println(verb + " " + uri + (body == null ? "" : " with body: " + body) + " ==> " + responseBody);

        if (responseBody == null) {
            responseBody = "{\"admin\":\"null\",\"token\":\"null\"}";
        }

        return parseJsonString(responseBody);
    }

    private static Map<String, String> parseJsonString(String jsonString) {
        Map<String, String> resultMap = new HashMap<>();

        String[] keyValuePairs = jsonString
                .substring(1, jsonString.length() - 1)
                .split(",");

        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            String key = entry[0].replace("\"", "").trim();
            String value = entry[1].replace("\"", "").trim();
            resultMap.put(key, value);
        }

        return resultMap;
    }

    public User authenticate(String email1, String password1) {
        if(email1 == null || password1 == null) return null;

        String query = "SELECT * FROM USER WHERE EMAIL = ? AND PASSWORD = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email1);
            preparedStatement.setString(2, password1);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String email = resultSet.getString("EMAIL");
                String name = resultSet.getString("NAME");
                String password = resultSet.getString("PASSWORD");
                int nif = resultSet.getInt("NIF");
                boolean isAdmin = resultSet.getBoolean("ISADMIN");

                return new User(name, nif, email, password, isAdmin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(User newUser) {
        if(newUser == null) return false;

        String query = "INSERT INTO USER (EMAIL, NAME, PASSWORD, NIF, ISADMIN) " +
                "SELECT ?, ?, ?, ?, ? " +
                "WHERE NOT EXISTS (SELECT 1 FROM USER WHERE EMAIL = ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newUser.getEmail());
            preparedStatement.setString(2, newUser.getName());
            preparedStatement.setString(3, newUser.getPassword());
            preparedStatement.setInt(4, newUser.getNif());
            preparedStatement.setBoolean(5, newUser.isAdmin());
            preparedStatement.setString(6, newUser.getEmail());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCode(int eventID, int minutes, String generatedCode) {
        if (minutes <= 0) return false;
        if (generatedCode == null || generatedCode.isEmpty()) return false;
        if (eventID <= 0) return false;

        String selectQuery = "SELECT * FROM EVENT WHERE ID = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, eventID);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    String updateQuery = "UPDATE EVENT SET CODE = ?, CODEEXPIRATIONTIME = ? WHERE ID = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, generatedCode);

                        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(minutes);
                        String formattedExpirationTime = expirationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                        updateStatement.setString(2, formattedExpirationTime);

                        updateStatement.setInt(3, eventID);

                        int rowsUpdated = updateStatement.executeUpdate();
                        return rowsUpdated > 0;
                    }
                } else
                    return false;
                }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createEvent(Event event) {
        if (event == null) return false;

        String insertEventSql = "INSERT INTO EVENT (NAME, LOCAL, DATE, BEGINHOUR, ENDHOUR) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEventSql)) {
            preparedStatement.setString(1, event.getName());
            preparedStatement.setString(2, event.getLocation());
            preparedStatement.setString(3, event.getDate());
            preparedStatement.setString(4, event.getStartTime());
            preparedStatement.setString(5, event.getEndTime());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Event> getAttendanceRecords_v2(String eventName, String location, String startDate, String endDate, String loggedUser) {
        List<Event> events = new ArrayList<>();

        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM EVENT_PARTICIPANT EP ");
            queryBuilder.append("INNER JOIN EVENT E ON EP.EVENT_ID = E.ID ");
            queryBuilder.append("INNER JOIN USER U ON EP.USER_EMAIL = U.EMAIL ");
            queryBuilder.append("WHERE 1=1 ");

            if (eventName != null && !eventName.isEmpty()) queryBuilder.append("AND E.NAME LIKE ? ");
            if (startDate != null && !startDate.isEmpty()) queryBuilder.append("AND E.DATE >= ? ");
            if (endDate != null && !endDate.isEmpty()) queryBuilder.append("AND E.DATE <= ? ");
            if (location != null && !location.isEmpty()) queryBuilder.append("AND E.LOCAL LIKE ? ");
            queryBuilder.append("AND U.EMAIL = ?");

            try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
                int parameterIndex = 1;

                if (eventName != null && !eventName.isEmpty()) preparedStatement.setString(parameterIndex++, "%" + eventName + "%");
                if (startDate != null && !startDate.isEmpty()) preparedStatement.setString(parameterIndex++, startDate);
                if (endDate != null && !endDate.isEmpty()) preparedStatement.setString(parameterIndex++, endDate);
                if (location != null && !location.isEmpty()) preparedStatement.setString(parameterIndex++, "%" + location + "%");
                preparedStatement.setString(parameterIndex, loggedUser);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Event event = new Event(
                                resultSet.getInt("ID"),
                                resultSet.getString("NAME"),
                                resultSet.getString("LOCAL"),
                                resultSet.getString("DATE"),
                                resultSet.getString("BEGINHOUR"),
                                resultSet.getString("ENDHOUR")
                        );
                        events.add(event);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public boolean checkIfEventCanBeEdited(int eventID) {
        String query = "SELECT COUNT(*) FROM EVENT_PARTICIPANT WHERE EVENT_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, eventID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int participationCount = resultSet.getInt(1);
                    return participationCount != 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Event deleteEvent(int eventID) {
        Event deletedEvent = getEventById(eventID);

        String query = "DELETE FROM EVENT WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, eventID);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0)
                return deletedEvent;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public List<User> getRecords(int eventID) {
        if (eventID <= 0) return new ArrayList<>();
        List<User> records = new ArrayList<>();

        String query = "SELECT U.* FROM EVENT_PARTICIPANT EP " +
                "INNER JOIN USER U ON EP.USER_EMAIL = U.EMAIL " +
                "WHERE EP.EVENT_ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, eventID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User(
                            resultSet.getString("NAME"),
                            resultSet.getInt("NIF"),
                            resultSet.getString("EMAIL"),
                            resultSet.getString("PASSWORD"),
                            resultSet.getBoolean("ISADMIN")
                    );
                    records.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public boolean checkIfUserExists(String parameter) {
        if (parameter == null || parameter.isEmpty()) return false;

        String query = "SELECT * FROM USER WHERE EMAIL = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, parameter);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkIfEventExists(int eventID) {

        String query = "SELECT * FROM EVENT WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, eventID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkIfEventExistsByAll(Event event) {
        if (event == null) return false;

        String query = "SELECT * FROM EVENT WHERE NAME = ? AND LOCAL = ? AND DATE = ? AND BEGINHOUR = ? AND ENDHOUR = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, event.getName());
            preparedStatement.setString(2, event.getLocation());
            preparedStatement.setString(3, event.getDate());
            preparedStatement.setString(4, event.getStartTime());
            preparedStatement.setString(5, event.getEndTime());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean addParticipant(int eventID, String parameter) {
        if(!checkIfUserExists(parameter) || !checkIfEventExists(eventID)) return false;

        String query = "INSERT INTO EVENT_PARTICIPANT (EVENT_ID, USER_EMAIL) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, eventID);
            preparedStatement.setString(2, parameter);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Event> getAllEvents(String eventName, String startDate, String endDate, String location) {
        List<Event> events = new ArrayList<>();

        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM EVENT");

            if (eventName != null && !eventName.isEmpty()) queryBuilder.append(" WHERE NAME LIKE ?");
            if (startDate != null && !startDate.isEmpty()) queryBuilder.append(" WHERE DATE >= ?");
            if (endDate != null && !endDate.isEmpty()) queryBuilder.append(" AND DATE <= ?");
            if (location != null && !location.isEmpty()) queryBuilder.append(" WHERE LOCAL LIKE ?");

            try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
                int parameterIndex = 1;

                if (eventName != null && !eventName.isEmpty()) preparedStatement.setString(parameterIndex++, "%" + eventName + "%");
                if (startDate != null && !startDate.isEmpty()) preparedStatement.setString(parameterIndex++, startDate);
                if (endDate != null && !endDate.isEmpty()) preparedStatement.setString(parameterIndex++, endDate);
                if (location != null && !location.isEmpty()) preparedStatement.setString(parameterIndex++, "%" + location + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Event event = new Event(
                                resultSet.getInt("ID"),
                                resultSet.getString("NAME"),
                                resultSet.getString("LOCAL"),
                                resultSet.getString("DATE"),
                                resultSet.getString("BEGINHOUR"),
                                resultSet.getString("ENDHOUR")
                        );
                        events.add(event);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return events;
    }

    public Event getEventById(int eventId) {
        Event event = null;
        String query = "SELECT * FROM EVENT WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, eventId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String eventName = resultSet.getString("NAME");
                    String eventLocal = resultSet.getString("LOCAL");
                    String eventDate = resultSet.getString("DATE");
                    String eventBeginHour = resultSet.getString("BEGINHOUR");
                    String eventEndHour = resultSet.getString("ENDHOUR");

                    event = new Event(eventId, eventName, eventLocal, eventDate, eventBeginHour, eventEndHour);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    public int getEventIdByCode(String eventCode) {
        if (eventCode == null || eventCode.isEmpty()) return -1;

        String query = "SELECT ID FROM EVENT WHERE CODE = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, eventCode);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean isParticipantRegistered(int eventId, String loggedUser) {
        String selectParticipantQuery = "SELECT 1 FROM EVENT_PARTICIPANT WHERE EVENT_ID = ? AND USER_EMAIL = ?";

        try (PreparedStatement selectParticipantStatement = connection.prepareStatement(selectParticipantQuery)) {
            selectParticipantStatement.setInt(1, eventId);
            selectParticipantStatement.setString(2, loggedUser);

            try (ResultSet participantResultSet = selectParticipantStatement.executeQuery()) {
                return participantResultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isCodeValid(String eventCode) {
        if (eventCode == null || eventCode.isEmpty()) return false;

        try {
            String selectEventQuery = "SELECT * FROM EVENT WHERE CODE = ?";
            try (PreparedStatement selectEventStatement = connection.prepareStatement(selectEventQuery)) {
                selectEventStatement.setString(1, eventCode);
                try (ResultSet eventResultSet = selectEventStatement.executeQuery()) {
                    if (eventResultSet.next()) {
                        LocalDateTime expirationTime = LocalDateTime.parse(eventResultSet.getString("CODEEXPIRATIONTIME"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        return expirationTime.isAfter(LocalDateTime.now());
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateCode() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int CODE_LENGTH = 5;
        String code = "";

        do {
            SecureRandom random = new SecureRandom();
            StringBuilder codeBuilder = new StringBuilder();

            for (int i = 0; i < CODE_LENGTH; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                char randomChar = CHARACTERS.charAt(randomIndex);
                codeBuilder.append(randomChar);
            }

            code = codeBuilder.toString();

        } while (getEventIdByCode(code) != -1);

        return code;
    }

    public static String generateBase64(String user, String password) {
        return String.valueOf(Base64.encode(user + ":" + password));
    }

    public static void validateDateFormat(String date, SimpleDateFormat dateFormat) throws ParseException {
        Date parsedDate = dateFormat.parse(date);

        // Se o parsing falhar, ou se a data fornecida não for igual à data formatada, lance uma exceção
        if (!date.equals(dateFormat.format(parsedDate))) {
            throw new ParseException("Formato de data inválido. Utilize o formato yyyy-MM-dd.", 0);
        }
    }
}
