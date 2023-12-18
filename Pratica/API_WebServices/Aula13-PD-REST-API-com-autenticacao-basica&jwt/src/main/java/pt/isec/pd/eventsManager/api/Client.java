package pt.isec.pd.eventsManager.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Client {
    private static BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
    private static PrintStream pout = System.out;

    public static void main(String[] args) throws IOException {
        TerminalManager terminalManager = new TerminalManager();

        terminalManager.processInput();
    }

}
