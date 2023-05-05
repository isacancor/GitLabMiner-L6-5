package aiss.gitlabminer.service;

import aiss.gitlabminer.model.*;
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
public class GitLabService {

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

    // POST apipath/{id}[?sinceCommits=5&sinceIssues=30&maxPages=2]
    /*
        sinceCommits: La operación devolverá los commits enviados en los últimos X días, siendo
            X el valor introducido como parámetro. Valor por defecto: 2.
        sinceIssues: La operación devolverá los issues actualizados en los últimos X días, siendo
            X el valor introducido como parámetro. Valor por defecto: 20.
        maxPages: Número máximo de páginas en los que se iterará en todos los casos. Valor
            por defecto: 2.
    */

    public Project genProject(String projectId, int sinceCommits, int sinceIssues, int maxPages) {
        Project newProject = getProjectById(projectId);
        if(maxPages <= 0){
            maxPages = maxPagesDefault;
        }

        // Calculate date and time to retrieve commits from based on the number of input days
        if (sinceCommits <= 0) {
            sinceCommits = sinceCommitsDefault;
        }
        List<Commit> commits = getCommits(projectId, sinceCommits, maxPages);

        // Calculate date and time to retrieve issues from based on the number of input days
        if (sinceIssues <= 0) {
            sinceIssues = sinceIssuesDefault;
        }
        List<Issue> issues = getIssues(projectId, sinceIssues, maxPages);

        newProject.setCommits(commits);
        newProject.setIssues(issues);

        return newProject;
    }



    // ----------------------------------------------------------------------------------------------------
    // Projects
    /*
        Projects. Implementará varias operaciones de lectura y escritura para añadir y listar
        proyectos. Entre otras, tendrá que implementar la operación POST adecuada para que los
        adaptadores puedan añadir datos de nuevos proyectos.
    */

    public Project getProjectById(String id){
        String uri = baseUri + id;
        Project project = restTemplate.getForObject(uri, Project.class);
        return project;
    }

    // ----------------------------------------------------------------------------------------------------
    // Commits
    /*
        Commits. Implementará, como mínimo, varias operaciones de lectura para listar todos los
        commits, y buscar commits por id.
     */

    public ResponseEntity<Commit[]> getCommitsRE(String uri) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Commit[]> request = new HttpEntity<>(null, headers);

        ResponseEntity<Commit[]> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, Commit[].class);

        return response;
    }

    public List<Commit> getCommits(String projectId, int sinceDays, int maxPages)
            throws HttpClientErrorException{
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

    // ----------------------------------------------------------------------------------------------------
    // Issues
    public ResponseEntity<Issue2[]> getIssuesRE(String uri){
        HttpHeaders headers = new HttpHeaders();
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
            newIssue.setComments(getComments(projectId, newIssue.getRefId(), maxPages));
            res.add(newIssue);
        }

        return res;
    }

    // ----------------------------------------------------------------------------------------------------
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
