import java.util.List;
import java.util.Map;

public class RestResponse {
    public String response;
    public Map<String, List<String>> responseHeader;

    public RestResponse(String response, Map<String, List<String>> responseHeader) {
        this.response = response;
        this.responseHeader = responseHeader;
    }
}
