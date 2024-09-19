import java.io.File;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class SearchFilesTask extends RecursiveTask<Map<String, String>> {
    private final File folder;
    private final String[] targetFormats;
    private final Map<String, String> matchingFiles;

    public SearchFilesTask(File folder, String[] targetFormats) {
        this.folder = folder;
        this.targetFormats = targetFormats;
        this.matchingFiles = new HashMap<>();
    }

    @Override
    protected Map<String, String> compute() {
        if (folder.isFile()) {
            String fileName = folder.getName();
            for (String targetFormat : targetFormats) {
                if (fileName.endsWith(targetFormat)) {
                    matchingFiles.put(fileName, folder.getAbsolutePath());
                    break;
                }
            }
        } else if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                List<SearchFilesTask> subTasks = new ArrayList<>();
                for (File file : files) {
                    SearchFilesTask subTask = new SearchFilesTask(file, targetFormats);
                    subTask.fork();
                    subTasks.add(subTask);
                }
                for (SearchFilesTask subTask : subTasks) {
                    matchingFiles.putAll(subTask.join());
                }
            }
        }
        return matchingFiles;
    }
}
