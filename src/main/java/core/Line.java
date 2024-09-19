package core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Line implements Comparable<Line> {

    private String number;
    private String name;
    private List<Station> stationsLine;

    public Line(String number, String name) {
        this.number = number;
        this.name = name;
        stationsLine = new ArrayList<>();
    }

    public void addStation(Station station) {
        stationsLine.add(station);
    }

    @Override
    public int compareTo(Line line) {
        return this.getNumber().compareTo(line.getNumber());
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Line) obj) == 0;
    }

    public String toString() {
        return name;
    }
}

