package aiss.gitlabminer.service;

import aiss.gitlabminer.model.Issue;
import aiss.gitlabminer.model.Issue2;
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
public class IssueService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CommentService commentService;

    @Value("${gitlabminer.baseuri}")
    private String baseUri;

    @Value("${gitlabminer.token}")
    private String token;

    // Issues
    public ResponseEntity<Issue2[]> getIssuesRE(String uri){
        HttpHeaders headers = new HttpHeaders();
        if(token != "") {
            headers.set("Authorization", "Bearer " + token);
        }

        HttpEntity<Issue2[]> request = new HttpEntity<>(null, headers);

        ResponseEntity<Issue2[]> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, Issue2[].class);

        return response;
    }

    public List<Issue> getIssues(String projectId, int sinceDays, int maxPages)
            throws HttpClientErrorException {
        List<Issue2> issues = new ArrayList<>();

        if (maxPages > 0) {
            // Since X Days
            ZonedDateTime sinceDate = ZonedDateTime.now().minusDays(sinceDays);
            String uri = baseUri + projectId + "/issues?created_after="
                    +sinceDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));

            // Pagination
            ResponseEntity<Issue2[]> response = getIssuesRE(uri);
            List<Issue2> pageIssues = Arrays.stream(response.getBody()).toList();
            issues.addAll(pageIssues);
            String nextPageURL = Util.getNextPageUrl(response.getHeaders());
            int page = 2;

            while(nextPageURL != null && page <= maxPages){
                response = getIssuesRE(nextPageURL);
                pageIssues = Arrays.stream(response.getBody()).toList();
                issues.addAll(pageIssues);

                nextPageURL = Util.getNextPageUrl(response.getHeaders());
                page++;
            }
        }

        List<Issue> res = new ArrayList<>();

        for (Issue2 issue: issues) {
            Issue newIssue = Util.parseIssue(issue);
            newIssue.setComments(commentService.getComments(projectId, newIssue.getRefId(), maxPages));
            res.add(newIssue);
        }

        return res;
    }

}
