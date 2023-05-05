package aiss.gitlabminer.controller;

import aiss.gitlabminer.model.Project;
import aiss.gitlabminer.service.GitLabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/gitlab")
public class GitLabController {

    @Autowired
    GitLabService service;

    @Autowired
    RestTemplate restTemplate;

    // GET /gitlabminer/{id}[?sinceCommits=5&sinceIssues=30&maxPages=2]
    @GetMapping("/{id}")
    public Project getProject(@PathVariable String id,
                              @RequestParam int sinceCommits, @RequestParam int sinceIssues,
                              @RequestParam int maxPages) {
        Project res = service.genProject(id, sinceCommits, sinceIssues,maxPages);
        return res;
    }

    // POST /gitlabminer/{id}[?sinceCommits=5&sinceIssues=30&maxPages=2]
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}")
    public Project sendProject(@PathVariable String id,
                              @RequestParam int sinceCommits, @RequestParam int sinceIssues,
                              @RequestParam int maxPages) {
        String uri = "http://localhost:8080/gitminer/projects";
        Project res = service.genProject(id, sinceCommits, sinceIssues,maxPages);

        ResponseEntity<Project> response = restTemplate
                .postForEntity(uri, res, Project.class);

        return response.getBody();
    }

}
