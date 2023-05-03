package aiss.gitlabminer.repository;

import aiss.gitlabminer.model.Project;
import aiss.gitlabminer.service.GitLabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository {

    @Autowired
    GitLabService service;

    public Project getProject(String projectId, int sinceCommits, int sinceIssues, int maxPages) {
        return service.genProject(projectId,sinceCommits,sinceIssues,maxPages);
    }
}
