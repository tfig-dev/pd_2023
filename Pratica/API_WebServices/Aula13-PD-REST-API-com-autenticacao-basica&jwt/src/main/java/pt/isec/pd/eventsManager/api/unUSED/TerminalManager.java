package pt.isec.pd.eventsManager.api.unUSED;

import pt.isec.pd.eventsManager.api.repository.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;

public class TerminalManager {

    private static String API_LINK = "http://localhost:8080",
            LOGIN = "/login", REGISTER = "/register", EVENTS = "/events", ATTENDANCES = "/attendances";

    private String name, username, email, password, tokenUser, location, date, startTime, endTime, startDate, endDate;
    private int nif, idEvent3, validity;
    private boolean isAdmin;
    private BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
    private PrintStream pout = System.out;

    public void displayMainMenu() {
        pout.println("---------------------");
        pout.println("1 - Login");
        pout.println("2 - Registo");
        pout.println("3 - SAIR");
        pout.println("Opção: ");
    }

    public void displayUserMenu() {
        pout.println("---------------------");
        pout.println("1 - Submeter Codigo");
        pout.println("2 - Consultar Presencas");
        pout.println("3 - SAIR");
        pout.println("Opção: ");
    }

    public void displayAdminMenu() {
        pout.println("---------------------");
        pout.println("1 - Criar Evento");
        pout.println("2 - Eliminar Evento");
        pout.println("3 - Consultar Eventos");
        pout.println("4 - Consultar Presencas");
        pout.println("5 - Gerar Codigo de Presencas");
        pout.println("6 - SAIR");
        pout.println("Opção: ");
    }

    public void displayFilterAdminMenu() {
        pout.println("---------------------");
        pout.println("1 - Filtrar por nome");
        pout.println("2 - Filtrar por local");
        pout.println("3 - Filtrar por datas");
        pout.println("4 - SAIR");
        pout.println("Opção: ");
    }

    public void displayFilterUserMenu() {
        pout.println("---------------------");
        pout.println("1 - Sem Filtro");
        pout.println("2 - Filtrar por nome");
        pout.println("3 - Filtrar por local");
        pout.println("4 - Filtrar por datas");
        pout.println("5 - SAIR");
        pout.println("Opção: ");
    }

    public void processUserInput() throws IOException {

        boolean continueProcessing = true;

        while (continueProcessing) {
            displayMainMenu();

            String userInput = bin.readLine();

            switch (userInput) {
                case "1":
                    pout.println("E-mail: ");
                    email = bin.readLine();

                    pout.println("Password: ");
                    password = bin.readLine();

                    String hashedKey = Data.generateBase64(email, password);
                    Map<String, String> responseMap = Data.sendLoginRequest(API_LINK + LOGIN,"GET", "basic " + hashedKey, null);

                    tokenUser = responseMap.get("token");
                    isAdmin = responseMap.get("admin").equalsIgnoreCase("true");

                    if (isAdmin)
                        processAdminMenu();
                    else
                        processUserMenu();

                    break;
                case "2":
                    pout.println("Nome: ");
                    username = bin.readLine();

                    pout.println("E-mail: ");
                    email = bin.readLine();

                    pout.println("Password: ");
                    password = bin.readLine();

                    try {
                        pout.println("NIF: ");
                        nif = Integer.parseInt(bin.readLine());
                    } catch (NumberFormatException e) {
                        pout.println("Invalid NIF");
                        break;
                    }

                    String registerBody = "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"nif\":" + nif + "}";
                    Data.sendRequestAndShowResponse(API_LINK + REGISTER, "POST", null, registerBody);

                    break;
                case "3":
                    pout.println("Exit");
                    tokenUser = "";
                    continueProcessing = false;
                    break;
                default:
                    pout.println("Invalid option");
                    break;
            }
        }


    }

    public void processAdminMenu() throws IOException {
        boolean continueAdminMenu = true;

        while (continueAdminMenu) {
            displayAdminMenu();

            String userInput = bin.readLine();

            switch (userInput) {
                case "1":
                    pout.println("Nome do evento: ");
                    name = bin.readLine();

                    pout.println("Local do evento: ");
                    location = bin.readLine();

                    pout.println("Data do evento (yyyy-mm-dd): ");
                    date = bin.readLine();

                    pout.println("Hora do inicio do evento (hh:mm): ");
                    startTime = bin.readLine();

                    pout.println("Hora do fim do evento (hh:mm): ");
                    endTime = bin.readLine();

                    String insertEventBody = "{\"name\":\"" + name + "\",\"location\":\"" + location + "\",\"date\":\"" + date + "\",\"startTime\":\"" + startTime + "\",\"endTime\":\"" + endTime + "\"}";

                    System.out.println(insertEventBody);

                    Data.sendRequestAndShowResponse(API_LINK + EVENTS, "POST", "bearer " + tokenUser, insertEventBody);

                    break;
                case "2":
                    pout.println("ID do evento a eliminar: ");
                    int id = Integer.parseInt(bin.readLine());

                    Data.sendRequestAndShowResponse(API_LINK + EVENTS + "/" + id, "DELETE", "bearer " + tokenUser, null);

                    break;
                case "3":
                    boolean continueFilterMenuAdmin = true;

                    while (continueFilterMenuAdmin) {
                        displayFilterAdminMenu();

                        String eventsFilter = bin.readLine();

                        switch (eventsFilter) {
                            case "1":
                                pout.println("Nome do evento: ");
                                String name = bin.readLine();

                                Data.sendRequestAndShowResponse(API_LINK + EVENTS + "?name=" + name, "GET", "bearer " + tokenUser, null);

                                break;
                            case "2":
                                pout.println("Local do evento: ");
                                String location = bin.readLine();

                                Data.sendRequestAndShowResponse(API_LINK + EVENTS + "?location=" + location, "GET", "bearer " + tokenUser, null);

                                break;
                            case "3":
                                pout.println("Data Inicial (yyyy-mm-dd): ");
                                startDate = bin.readLine();

                                pout.println("Data Final (yyyy-mm-dd): ");
                                endDate = bin.readLine();

                                Data.sendRequestAndShowResponse(API_LINK + EVENTS + "?startDate=" + startDate + "&endDate=" + endDate, "GET", "bearer " + tokenUser, null);

                                break;
                            case "4":
                                continueFilterMenuAdmin = false;
                                break;
                            default:
                                pout.println("Invalid option");
                                break;
                        }
                    }

                    break;
                case "4":
                    pout.println("ID do evento: ");
                    int idEvent2 = Integer.parseInt(bin.readLine());

                    Data.sendRequestAndShowResponse(API_LINK + ATTENDANCES + "/" + idEvent2, "GET", "bearer " + tokenUser, null);

                    break;
                case "5":
                    try {
                        pout.println("ID do evento: ");
                        idEvent3 = Integer.parseInt(bin.readLine());
                    } catch (NumberFormatException e) {
                        pout.println("Informação inválida.");
                        break;
                    }

                    try {
                        pout.println("Validade do código (em minutos): ");
                        validity = Integer.parseInt(bin.readLine());
                    } catch (NumberFormatException e) {
                        pout.println("Informação inválida.");
                        break;
                    }

                    String insertCodeBody = "{\"timeout\":" + validity + "}";
                    Data.sendRequestAndShowResponse(API_LINK + EVENTS + "/" + idEvent3, "PUT", "bearer " + tokenUser, insertCodeBody);

                    break;
                case "6":
                    continueAdminMenu = false;
                    break;
                default:
                    pout.println("Invalid option");
                    break;
            }

        }

    }

    public void processUserMenu() throws IOException {
        boolean continueUserMenu = true;

        while (continueUserMenu) {
            displayUserMenu();

            String userInput = bin.readLine();

            switch (userInput) {
                case "1":
                    pout.println("Código: ");
                    String code = bin.readLine();

                    String insertCodeBody = "{\"code\":\"" + code + "\"}";
                    Data.sendRequestAndShowResponse(API_LINK + ATTENDANCES, "POST", "bearer " + tokenUser, insertCodeBody);

                    break;
                case "2":
                    boolean continueFilterMenuUser = true;

                    while (continueFilterMenuUser) {
                        displayFilterUserMenu();

                        String eventsFilter = bin.readLine();

                        switch (eventsFilter) {
                            case "1":
                                Data.sendRequestAndShowResponse(API_LINK + ATTENDANCES, "GET", "bearer " + tokenUser, null);

                                break;
                            case "2":
                                pout.println("Nome do evento: ");
                                String name = bin.readLine();

                                Data.sendRequestAndShowResponse(API_LINK + ATTENDANCES + "?name=" + name, "GET", "bearer " + tokenUser, null);

                                break;
                            case "3":
                                pout.println("Local do evento: ");
                                String location = bin.readLine();

                                Data.sendRequestAndShowResponse(API_LINK + ATTENDANCES + "?location=" + location, "GET", "bearer " + tokenUser, null);

                                break;
                            case "4":
                                pout.println("Data Inicial (yyyy-mm-dd): ");
                                startDate = bin.readLine();

                                pout.println("Data Final (yyyy-mm-dd): ");
                                endDate = bin.readLine();

                                Data.sendRequestAndShowResponse(API_LINK + ATTENDANCES + "?startDate=" + startDate + "&endDate=" + endDate, "GET", "bearer " + tokenUser, null);

                                break;
                            case "5":
                                continueFilterMenuUser = false;
                                break;
                            default:
                                pout.println("Invalid option");
                                break;
                        }
                    }

                    break;
                case "3":
                    continueUserMenu = false;
                    break;
                default:
                    pout.println("Invalid option");
                    break;

            }
        }
    }

}
