package core;

import lombok.Data;

@Data
public class Station implements Comparable<Station> {
    private String name;
    private Line line;
    private double depth;
    private String openDate;
    private boolean hasConnection;

    public Station() {
        this.name = null;
        this.line = null;
        this.depth = 100.0;
        this.openDate = null;
        this.hasConnection = false;
    }

    @Override
    public int compareTo(Station station) {
        int lineComparison = line.compareTo(station.getLine());
        if (lineComparison != 0) {
            return lineComparison;
        }
        return name.compareToIgnoreCase(station.getName());
    }

    public String toString() {
        return "\tname: " + getName() + "\n" +
                "\tline: " + getLine() + "\n" +
                "\tdate: " + getOpenDate() + "\n" +
                "\tdepth: " + getDepth() + "\n" +
                "\tпересадка: " + isHasConnection() + "\n";
    }

}
