public class Main {
    
    public static void main(String[] args) throws Exception {
        String url = "https://skillbox-java.github.io/";
        String directory = "C:\\Users\\study\\Desktop\\искать";
        MetroInfo metroMoscow = new MetroInfo();
        
        metroMoscow.parsHTML(url);
        metroMoscow.searchFiles(directory);
        metroMoscow.recordInfo();
    }
}
