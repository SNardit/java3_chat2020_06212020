package client.history;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class History {
    private static PrintWriter out;

    private static String getHistoryFileByLogin (String login) {
        return "NetworkClient/history/history_" + login + ".txt";
    }

    public static void start (String login) {
        try {
            out = new PrintWriter(new FileOutputStream(getHistoryFileByLogin(login), true));
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
    }

    public static void stop(){
        if (out != null) {
            out.close();
        }
    }

    public static void writeLine (String message) {
        out.println(message);
    }

    public static String getLast100LinesOfHistory (String login) {
        if (!Files.exists(Paths.get(getHistoryFileByLogin(login)))) {
            return " ";
        }

        StringBuilder sb = new StringBuilder();
        try {
            List<String> historyLines = Files.readAllLines(Paths.get(getHistoryFileByLogin(login)));
            int startPosition = 0;
            if (historyLines.size() > 3) {
                startPosition = historyLines.size() - 3;
            }
            for (int i = startPosition; i < historyLines.size(); i++) {
                sb.append(historyLines.get(i)).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
