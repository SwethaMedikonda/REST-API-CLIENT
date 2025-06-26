import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {
    public static void main(String[] args) {
        try {
            // Sample coordinates for New York City
            double latitude = 40.7128;
            double longitude = -74.0060;

            // Open-Meteo API endpoint (no API key required)
            String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" 
                                + latitude + "&longitude=" + longitude 
                                + "&current_weather=true";

            // Create URL and open connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Read response
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // Close connections
                in.close();
                conn.disconnect();

                // Simple JSON Parsing (without libraries)
                String json = content.toString();
                System.out.println("Raw JSON Response:\n" + json + "\n");

                // Extract current temperature and windspeed
                String currentWeather = json.split("\"current_weather\":\\{")[1].split("}")[0];

                String temperature = extractValue(currentWeather, "temperature");
                String windspeed = extractValue(currentWeather, "windspeed");
                String weatherCode = extractValue(currentWeather, "weathercode");

                // Display structured output
                System.out.println("=== Current Weather ===");
                System.out.println("Temperature : " + temperature + "°C");
                System.out.println("Windspeed   : " + windspeed + " km/h");
                System.out.println("WeatherCode : " + weatherCode);
            } else {
                System.out.println("Failed to fetch weather data. Response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to extract values from JSON-like string
    private static String extractValue(String json, String key) {
        String[] parts = json.split("\"" + key + "\":");
        if (parts.length > 1) {
            return parts[1].split(",")[0].replaceAll("[^\\d.\\-]", "");
        }
        return "N/A";
    }
}
