import core.Station;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ParseJSON {
    private final List<Station> stationsDepth;

    public ParseJSON() {
        this.stationsDepth = new ArrayList<>();
    }

    public void addStation(Station station) { //метод отсечение копий
        String name = station.getName();
        double depth = station.getDepth();

        boolean stationExists = stationsDepth.stream()
                .anyMatch(s -> s.getName().equals(name) && s.getDepth() == depth);
        if (!stationExists) {
            stationsDepth.add(station);
        }
    }

    public Double transformationDouble(String s) {
        double defaultDepth = 100.0;
        try {
            String str = s.replace(",", ".");
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            System.err.println(s + " - unable to convert character to double" );
            return defaultDepth;
        }
    }

    public void createStationDepth(String jsonFile) {

        JSONParser parser = new JSONParser();
        try {
            FileReader fileReader = new FileReader(jsonFile, StandardCharsets.UTF_8);
            Object obj = parser.parse(fileReader);
            JSONArray stationsArray = (JSONArray) obj;

            for (Object stationObject : stationsArray) {
                JSONObject stationJsonObject = (JSONObject) stationObject;

                String stationName = (String) stationJsonObject.get("station_name");
                String depthString = (String) stationJsonObject.get("depth");
                double depth = transformationDouble(depthString);
                Station stationA = new Station();
                stationA.setName(stationName);
                stationA.setDepth(depth);
                addStation(stationA);
            }
        } catch (FileNotFoundException | MalformedInputException ex) {

            System.err.println("JSON-file not found or damaged" + ex.getMessage());
        } catch (ParseException ex) {
            System.err.println("parsing JSON-file error: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public String toStringJSON() {
        StringBuilder builder = new StringBuilder();
        for (Station station : stationsDepth) {
            builder.append(station.getName()).append("  depth ->   ").append(station.getDepth()).append("\n");
        }
        return builder.toString();
    }

    public String printStationsWithSameName() { // метод проверки копий
        Map<String, List<Station>> stationsByName = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        for (Station station : stationsDepth) {
            String name = station.getName();
            if (!stationsByName.containsKey(name)) {
                stationsByName.put(name, new ArrayList<>());
            }
            stationsByName.get(name).add(station);
        }

        for (Map.Entry<String, List<Station>> entry : stationsByName.entrySet()) {
            List<Station> stations = entry.getValue();


            for (Station station : stations) {
                builder.append(station.getName()).append("  depth ->   ").append(station.getDepth()).append("\n");
            }
        }
        return builder.toString();
    }
}
