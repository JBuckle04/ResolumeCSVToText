import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class App {
    public static void main(String[] args) throws Exception {
        String filePath = "./src/TextExamples.csv"; // Replace with your CSV file path

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Assuming CSV is comma-separated
                for (String value : values) {
                    System.out.print(value);
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendPostRequest("http://localhost:8080/api/v1/composition/layers/1/clips/1", "TEST");
    }

    private static void sendPostRequest(String apiUrl, String payload) {
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonPayload = "{\"data\": \"" + payload.replace("\"", "\\\"") + "\"}"; // Wrap data in JSON format

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            // Read response (optional)
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("Response: " + response.toString());
            }
            
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
