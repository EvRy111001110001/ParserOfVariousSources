import core.Line;
import core.Station;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;


@Data
public class MetroInfo {
    private Map<String, Line> lineMetro;
    private TreeSet<Station> stationMetro;

    public MetroInfo() {
        lineMetro = new HashMap<>();
        stationMetro = new TreeSet<>();
    }

    public List<String> exceptionStation() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Арбатская");
        stringList.add("Смоленская");
        return stringList;
    }

    public void parsHTML(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        ParseHTML parseHTML = new ParseHTML();

        parseHTML.setRecordInfo(doc);

        lineMetro = parseHTML.getLineMap();
        stationMetro = parseHTML.getStations();

    }

    public void searchFiles(String directory) {
        ParseJSON parseJSON = new ParseJSON();
        ParseCSV parseCSV = new ParseCSV();
        String[] targetFormats = {".json", ".csv"};

        SearchFilesTask task = new SearchFilesTask(new File(directory), targetFormats);
        ForkJoinPool pool = new ForkJoinPool();
        Map<String, String> matchingFiles = pool.invoke(task);

        for (Map.Entry<String, String> entry : matchingFiles.entrySet()) {
            if (entry.getKey().endsWith(targetFormats[0])) {
                parseJSON.createStationDepth(entry.getValue());
                System.out.println("JSON-file - " + entry.getValue());
            } else if (entry.getKey().endsWith(targetFormats[1])) {
                parseCSV.createStationOpenDate(entry.getValue());
                System.out.println("CSV-file - " + entry.getValue());
            }
        }

        List<Station> jsonFiles = (parseJSON.getStationsDepth());
        parseJSONFiles(jsonFiles);
        List<Station> csvFiles = parseCSV.getStationsOpenDate();
        parseCSVFiles(csvFiles);

    }

    public void parseJSONFiles(List<Station> jsonFiles) {

        for (Station stationY : depthMaxJson(jsonFiles)) {
            stationMetro.stream()
                    .filter(station -> station.getName().equals(stationY.getName()) && station.getDepth() == 100.0)
                    .forEach(station -> station.setDepth(stationY.getDepth()));
        }
    }

    public List<Station> depthMaxJson(List<Station> jsonFiles) {//возвращает список станций, в котором содержатся станции с уникальными именами и те станции, у которых параметр Depth является максимальным среди станций с одинаковыми именами.
        Map<String, Double> maxParameterMap = new HashMap<>();
        List<Station> result = new ArrayList<>();
        try {
            for (Station stationDepth : jsonFiles) {
                if (exceptionStation().contains(stationDepth.getName())) {
                    result.add(stationDepth);
                } else {
                    if (!maxParameterMap.containsKey(stationDepth.getName()) || stationDepth.getDepth() < maxParameterMap.get(stationDepth.getName())) {
                        maxParameterMap.put(stationDepth.getName(), stationDepth.getDepth());
                    }
                }
            }

            for (Station stationX : jsonFiles) {
                Double maxDepth = maxParameterMap.get(stationX.getName());

                if (maxDepth != null && stationX.getDepth() == maxDepth) {
                    if (stationX.getDepth() == maxParameterMap.get(stationX.getName())) {
                        result.add(stationX);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.err.println();
        }
        return result;
    }


    public void parseCSVFiles(List<Station> csvFiles) {
        for (Station stationB : stationMetro) {
            Station stationC = csvFiles.stream()
                    .filter(s -> s.getName().equals(stationB.getName()))
                    .findFirst()
                    .orElse(null);
            if (stationC != null && stationB.getOpenDate() == null) {
                csvFiles.remove(stationC);
                stationB.setOpenDate(stationC.getOpenDate());
            }
        }
    }

    public void recordInfo() {
        RecordJSONFiles recordJSONFiles = new RecordJSONFiles();
        try {
            recordJSONFiles.recordLineMetro(lineMetro);
            recordJSONFiles.recordStationMetro(stationMetro);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toStringLine() {
        StringBuilder builder = new StringBuilder();
        lineMetro.forEach((srt, line) -> {
            builder.append(line.toString()).append("\n");
            line.getStationsLine().forEach(station ->
                builder.append(station.toString()));
        });
        return builder.toString();
    }

    public String toStringStation() {
        StringBuilder builder = new StringBuilder();
        stationMetro.forEach(station ->
                builder.append(station.toString()));

        return builder.toString();
    }
}
