import core.Station;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ParseCSV {
    private final List<Station> stationsOpenDate;

    public ParseCSV() {
        this.stationsOpenDate = new ArrayList<>();
    }

    public void createStationOpenDate(String csvFile) {
        List<String> stringsCSV = new ArrayList<>();
        try {
            stringsCSV = Files.readAllLines(Paths.get(csvFile), StandardCharsets.UTF_8);
        } catch (FileNotFoundException | MalformedInputException ex) {
            System.err.println("CSV-file not found or damaged " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String firstLine = null;
        for (String str : stringsCSV) {
            if (firstLine == null) {
                firstLine = str;
                continue;
            }
            String[] fragments = str.split(",");
            if (fragments.length >= 2) {
                String stationName = fragments[0];
                String stationOpenDate = fragments[1];

                Station station = new Station();
                station.setName(stationName);
                station.setOpenDate(stationOpenDate);
                stationsOpenDate.add(station);
            }
        }
    }


    public String toStringCSV() {
        StringBuilder builder = new StringBuilder();
        for (Station station : stationsOpenDate) {
            builder.append(station.getName()).append("  date ->   ").append(station.getOpenDate()).append("\n");
        }

        return builder.toString();
    }

}
