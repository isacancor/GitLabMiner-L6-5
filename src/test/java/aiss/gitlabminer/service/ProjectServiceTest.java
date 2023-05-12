package aiss.gitlabminer.service;

import aiss.gitlabminer.exception.ProjectNotFoundException;
import aiss.gitlabminer.model.Comment;
import aiss.gitlabminer.model.Commit;
import aiss.gitlabminer.model.Issue;
import aiss.gitlabminer.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    ProjectService service;

    // Projects
    @Test
    @DisplayName("Generate Project")
    void genProject() throws ProjectNotFoundException {
        String id = "4207231";
        int sinceCommits = 10;
        int sinceIssues = 2;
        int maxPages = 3;
        Project project = service.genProject(id,sinceCommits, sinceIssues, maxPages);
        assertEquals(project.getId(), id, "The id doesn't match");
        assertEquals(project.getName(), "graphviz", "The name doesn't match");
        assertEquals(project.getWebUrl(),  "https://gitlab.com/graphviz/graphviz", "The web doesn't match");
        assertNotNull(project.getCommits(), "The list of commits is null");
        assertNotNull(project.getIssues(), "The list of issues is null");
        assertTrue(project.getCommits().size() <= 20*maxPages, "There are more commit pages than specified");
        assertTrue(project.getIssues().size() <= 20*maxPages, "There are more issue pages than specified");
        assertTrue(project.getIssues().stream().allMatch(i->i.getComments().size() <= 20*maxPages), "There are more comment pages than specified");

        project.prettyPrint();
    }

    @Test
    @DisplayName("Get Project By Id")
    void getProjectById() throws ProjectNotFoundException {
        String id = "4207231";
        Project project = service.getProjectById(id);
        assertEquals(project.getId(), id, "The id doesn't match");
        assertEquals(project.getName(), "graphviz", "The name doesn't match");
        assertEquals(project.getWebUrl(),  "https://gitlab.com/graphviz/graphviz", "The web doesn't match");
        assertNotNull(project.getCommits(), "The list of commits is null");
        assertNotNull(project.getIssues(), "The list of issues is null");
        assertTrue(project.getCommits().isEmpty(), "The list of commits is not empty");
        assertTrue(project.getIssues().isEmpty(), "The list of issues is not empty");

        project.prettyPrint();
    }

}