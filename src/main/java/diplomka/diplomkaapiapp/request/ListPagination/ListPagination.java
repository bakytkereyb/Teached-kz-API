package diplomka.diplomkaapiapp.request.ListPagination;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListPagination<T> {
    private List<T> list;
    private boolean hasMore;
}
