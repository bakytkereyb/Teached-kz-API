package diplomka.diplomkaapiapp.controllers.scopus;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/scopus")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Scopus", description = "APIs for operation on Scopus")
public class ScopusController {

    @GetMapping("")
    public ResponseEntity getScopus(
            @RequestParam("query") String query,
            @RequestParam("start") Integer start,
            @RequestParam("count") Integer count
    ) {
        WebClient.Builder builder = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)); // 5 MB

        String uri = String.format("https://api.elsevier.com/content/search/author?apiKey=054274153af1b7926debe44390477b4f&httpAccept=application/json&query=SUBJAREA(%s)&start=%s&count=%s",
                query, start, count);

        log.info(uri);

        String body = builder
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(body);
    }
}
