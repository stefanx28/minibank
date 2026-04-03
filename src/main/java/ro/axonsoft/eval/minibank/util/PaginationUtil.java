package ro.axonsoft.eval.minibank.util;
import org.springframework.data.domain.Page;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Function;

public class PaginationUtil {

    public static <T, R> Map<String, Object> toPaginatedResponse(Page<T> page, Function<T, R> mapper) {
        Map<String, Object> response = new LinkedHashMap<>();
        List<R> content = page.getContent().stream()
                .map(mapper)
                .toList();

        response.put("content", content);
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("number", page.getNumber());
        response.put("size", page.getSize());

        return response;
    }
}