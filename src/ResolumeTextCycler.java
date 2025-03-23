import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ResolumeTextCycler {
    private static final String API_URL = "http://localhost:8080/api/v1/composition/layers/1/clips/1";
    private static final String csvPath = "./src/TextExamples.csv";
    private static List<String> messages = null; // Messages to cycle
    private static final int seconds = 3; //Enter value in seconds to change the text
    private static int index = 0;

    public static void main(String[] args) {

        File csvFile = new File(csvPath);
        if(!csvFile.exists())
        {
            System.out.println("File not found:" + csvFile.getAbsolutePath());
            System.exit(-1);
        }

        messages = readSingleColumnCSV(csvPath);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendPutRequest(messages.get(index));
                index = (index + 1) % messages.size(); // Cycle through messages
                Collections.shuffle(messages);
            }
        }, 0, seconds * 1000); 
    }

    private static void sendPutRequest(String message) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");  // Changed from POST to PUT
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create JSON payload
            String jsonPayload = "{\"video\":{\"sourceparams\":{\"Text\": \"" + message + "\"}}}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Sent: " + message + " | Response Code: " + responseCode);
        } 
        catch (Exception e) {
        System.err.println("Error sending request: " + e.getMessage());
        }
    }

    public static List<String> readSingleColumnCSV(String filePath) {
        List<String> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.trim()); // Trim to remove extra spaces
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data; // Convert List to String array
    }

}
