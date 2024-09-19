import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.Line;
import core.Station;

import java.io.FileWriter;
import java.util.Map;
import java.util.TreeSet;

public class RecordJSONFiles {


    public void recordLineMetro(Map<String, Line> lineMetro) throws Exception { //через GSON
        FileWriter out = new FileWriter("mapMoscowMetro.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject metroData = new JsonObject();
        JsonArray linesArray = new JsonArray();

        for (Line line : lineMetro.values()) {
            JsonObject lineGson = new JsonObject();
            lineGson.addProperty("number", line.getNumber());
            lineGson.addProperty("name", line.getName());
            linesArray.add(lineGson);
        }

        JsonObject lineGson2 = new JsonObject();
        for (Line line : lineMetro.values()) {
            JsonArray stationsArray = new JsonArray();
            for (Station station : line.getStationsLine()) {
                stationsArray.add(station.getName());
            }
            lineGson2.add(line.getNumber(), stationsArray);
        }

        metroData.add("lines", linesArray);
        metroData.add("stations", lineGson2);

        gson.toJson(metroData, out);
        out.close();
    }

    public void recordStationMetro(TreeSet<Station> stations) throws Exception {// через Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonData = objectMapper.createObjectNode();


        ArrayNode stationArray = objectMapper.createArrayNode();

        for (Station stat : stations) {
            ObjectNode stationNode = objectMapper.createObjectNode();
            stationNode.put("name", stat.getName());
            stationNode.put("line", String.valueOf(stat.getLine()));
            if (stat.getOpenDate() != null) {
                stationNode.put("date", stat.getOpenDate());
            }
            if (stat.getDepth() != 100.0) {
                stationNode.put("depth", stat.getDepth());
            }
            if (stat.isHasConnection()) {
                stationNode.put("hasConnection", true);
            }
            stationArray.add(stationNode);
        }
        jsonData.set("stations", stationArray);

        FileWriter out = new FileWriter("MoscowMetroStations.json");
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());

        writer.writeValue(out, jsonData);
        out.close();
    }
}
