import core.Line;
import core.Station;

import lombok.Getter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


@Getter
public class ParseHTML {
    private final Map<String, Line> lineMap;
    private final TreeSet<Station> stations;

    public ParseHTML() {
        lineMap = new HashMap<>();
        stations = new TreeSet<>();
    }

    public void setRecordInfo(Document doc) {
        parsLine(doc);
        parsStation(doc);
    }

    public void addInStations(Station station) {
        stations.add(station);
    }

    public void addLine(Line line) {
        lineMap.put(line.getNumber(), line);
    }

    public void parsStation(Document doc) {
        Elements elLines = doc.select("div.js-metro-stations");
        elLines.forEach(element -> {
            String numberLine2 = element.attr("data-line");
            Line lineA = lineMap.get(numberLine2);
            Elements elStations = element.select(".single-station");
            for (Element elStation : elStations) {
                String nameStation = elStation.select("span.name").text();
                boolean hasConnection = elStation.select("span.t-icon-metroln").hasAttr("title");
                Station stationL = new Station();
                stationL.setName(nameStation);
                stationL.setLine(lineA);
                stationL.setHasConnection(hasConnection);
                lineA.addStation(stationL);
                addInStations(stationL);
            }
        });
    }

    public void parsLine(Document doc) {

        Elements elLines = doc.select(".js-metro-line");
        for (Element elLine : elLines) {
            String numberLine = elLine.attr("data-line");
            String nameLine = elLine.select("span").text();
            Line line = new Line(numberLine, nameLine);
            addLine(line);
        }
    }
}
