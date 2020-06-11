import java.util.List;

public class CloudFileInfo {
    public String filename;
    public List<String> classes;
    public List<String> tags;
    public String date;
    public String size;
    public String id;

    public CloudFileInfo(String filename, String date, String size, String id, List<String> classes, List<String> tags) {
        this.filename = filename;
        this.date = date;
        this.size = size;
        this.id = id;
        this.classes = classes;
        this.tags = tags;
    }
}
