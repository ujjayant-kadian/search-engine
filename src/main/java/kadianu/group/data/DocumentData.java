package kadianu.group.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentData {
    private String id;
    private String title;
    private String author;
    private String content;
}
