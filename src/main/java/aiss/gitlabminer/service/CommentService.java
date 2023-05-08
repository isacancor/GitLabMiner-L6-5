package aiss.gitlabminer.service;

import aiss.gitlabminer.model.Comment;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${gitlabminer.baseuri}")
    private String baseUri;

    @Value("${gitlabminer.token}")
    private String token;

    @Value("${gitminer.sincecommits}")
    private int sinceCommitsDefault;

    @Value("${gitminer.sinceissues}")
    private int sinceIssuesDefault;

    @Value("${gitminer.maxpages}")
    private int maxPagesDefault;

    // Comments
    /*
        Comments. Implementará, como mínimo, varias operaciones de lectura para listar todos
        los comentarios y buscar comentarios por id.
    */
    public List<Comment> getComments2(String projectId, String issueIID){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Comment[]> request = new HttpEntity<>(null, headers);

        String uri = baseUri + projectId + "/issues/" + issueIID + "/notes";

        ResponseEntity<Comment[]> comments = restTemplate
                .exchange(uri, HttpMethod.GET, request, Comment[].class);

        return Arrays.stream(comments.getBody()).toList();
    }

    public ResponseEntity<Comment[]> getCommentRE(String uri){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Comment[]> request = new HttpEntity<>(null, headers);

        ResponseEntity<Comment[]> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, Comment[].class);

        return response;
    }

    public List<Comment> getComments(String projectId, String issueIID, int maxPages)
            throws HttpClientErrorException {
        List<Comment> comments = new ArrayList<>();

        if (maxPages > 0) {
            String uri = baseUri + projectId + "/issues/" + issueIID + "/notes";

            // Pagination
            ResponseEntity<Comment[]> response = getCommentRE(uri);
            List<Comment> pageComments = Arrays.stream(response.getBody()).toList();
            comments.addAll(pageComments);
            String nextPageURL = Util.getNextPageUrl(response.getHeaders());
            int page = 2;

            while(nextPageURL != null && page <= maxPages){
                response = getCommentRE(nextPageURL);
                pageComments = Arrays.stream(response.getBody()).toList();
                comments.addAll(pageComments);

                nextPageURL = Util.getNextPageUrl(response.getHeaders());
                page++;
            }
        }

        return comments;
    }

}
