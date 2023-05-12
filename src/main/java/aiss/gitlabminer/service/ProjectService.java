package aiss.gitlabminer.service;

import aiss.gitlabminer.exception.ProjectNotFoundException;
import aiss.gitlabminer.model.Commit;
import aiss.gitlabminer.model.Issue;
import aiss.gitlabminer.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CommitService commitService;
    @Autowired
    IssueService issueService;

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

    public Project genProject(String projectId, int sinceCommits, int sinceIssues, int maxPages) throws ProjectNotFoundException {
        Project newProject = getProjectById(projectId);
        if(maxPages <= 0){
            maxPages = maxPagesDefault;
        }

        // Calculate date and time to retrieve commits from based on the number of input days
        if (sinceCommits <= 0) {
            sinceCommits = sinceCommitsDefault;
        }
        List<Commit> commits = commitService.getCommits(projectId, sinceCommits, maxPages);

        // Calculate date and time to retrieve issues from based on the number of input days
        if (sinceIssues <= 0) {
            sinceIssues = sinceIssuesDefault;
        }
        List<Issue> issues = issueService.getIssues(projectId, sinceIssues, maxPages);

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

    public Project getProjectById(String id) throws ProjectNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        if (token != "") {
            headers.set("Authorization", "Bearer " + token);
        }

        HttpEntity<Project[]> request = new HttpEntity<>(null, headers);

        String uri = baseUri + id;
        Project project;
        try {
            project = restTemplate
                    .exchange(uri, HttpMethod.GET, request, Project.class).getBody();
        } catch (Exception e) {
            throw new ProjectNotFoundException();
        }

        return project;
    }

}
