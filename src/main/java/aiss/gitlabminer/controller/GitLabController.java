package aiss.gitlabminer.controller;

import aiss.gitlabminer.model.Project;
import aiss.gitlabminer.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gitlabminer/")
public class GitLabController {

    @Autowired
    ProjectRepository repository;

    // GET /gitlabminer/{id}[?sinceCommits=5&sinceIssues=30&maxPages=2]
    @GetMapping("/{id}/")
    public Project getProject(@PathVariable String id,
                              @RequestParam int sinceCommits, @RequestParam int sinceIssues,
                              @RequestParam int maxPages) {
        Project res = repository.getProject(id, sinceCommits, sinceIssues,maxPages);
        return res;
    }

}
