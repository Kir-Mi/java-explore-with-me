package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatRequest;
import ru.practicum.util.DateUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsClient extends BaseClient {

    private final DateUtil dateUtil;

    public StatisticsClient(@Value("${stats-server.url}") String serverUrl,
                            RestTemplateBuilder builder,
                            DateUtil dateUtil) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
        this.dateUtil = dateUtil;
    }

    public ResponseEntity<Object> saveStatistics(String app, String uri, String ip, LocalDateTime timestamp) {
        StatRequest request = StatRequest.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build();
        return post("/hit", request);
    }

    public ResponseEntity<Object> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String startEncoded = dateUtil.encodeToString(start);
        String endEncoded = dateUtil.encodeToString(end);
        Map<String, Object> params = new HashMap<>();
        params.put(Utils.START_PARAM, startEncoded);
        params.put(Utils.END_PARAM, endEncoded);
        StringBuilder sb = new StringBuilder();
        sb.append("/stats")
                .append("?")
                .append("start={start}")
                .append("&")
                .append("end={end}");
        if (uris != null && !uris.isEmpty()) {
            String uriString = String.join(",", uris);
            params.put(Utils.URIS_PARAM, uriString);
            sb.append("&")
                    .append("uris={uris}");
        }
        if (unique != null) {
            params.put(Utils.UNIQUE_PARAM, unique);
            sb.append("&")
                    .append("unique={unique}");
        }
        return get(sb.toString(), params);
    }
}