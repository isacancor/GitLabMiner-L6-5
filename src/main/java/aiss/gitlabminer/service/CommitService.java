package aiss.gitlabminer.service;

import aiss.gitlabminer.model.Commit;
import aiss.gitlabminer.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CommitService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${gitlabminer.baseuri}")
    private String baseUri;

    @Value("${gitlabminer.token}")
    private String token;

    // Commits
    /*
        Commits. Implementará, como mínimo, varias operaciones de lectura para listar todos los
        commits, y buscar commits por id.
     */

    public ResponseEntity<Commit[]> getCommitsRE(String uri) {
        HttpHeaders headers = new HttpHeaders();
        if(token != "") {
            headers.set("Authorization", "Bearer " + token);
        }

        HttpEntity<Commit[]> request = new HttpEntity<>(null, headers);

        ResponseEntity<Commit[]> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, Commit[].class);

        return response;
    }

    public List<Commit> getCommits(String projectId, int sinceDays, int maxPages)
            throws HttpClientErrorException {
        List<Commit> commits = new ArrayList<>();

        if (maxPages > 0) {
            // Since X Days
            ZonedDateTime sinceDate = ZonedDateTime.now().minusDays(sinceDays);
            String uri = baseUri + projectId + "/repository/commits?since="
                    + sinceDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));

            // Pagination
            ResponseEntity<Commit[]> response = getCommitsRE(uri);
            List<Commit> pageCommits = Arrays.stream(response.getBody()).toList();
            commits.addAll(pageCommits);
            String nextPageURL = Util.getNextPageUrl(response.getHeaders());
            int page = 2;

            while (nextPageURL != null && page <= maxPages) {
                response = getCommitsRE(nextPageURL);
                pageCommits = Arrays.stream(response.getBody()).toList();
                commits.addAll(pageCommits);
                nextPageURL = Util.getNextPageUrl(response.getHeaders());
                page++;
            }
        }

        return commits;
    }

}
