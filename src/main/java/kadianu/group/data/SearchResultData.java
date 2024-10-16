package kadianu.group.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultData {
    private String id;
    private float score;
    private String title;
    private String content;
}