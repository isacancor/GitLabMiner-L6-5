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

    @Test
    @DisplayName("Generate Project")
    void genProject() throws ProjectNotFoundException {
        String id = "4207231";
        Project project = service.genProject(id,10,2,0);
        assertEquals(project.getId(), id, "The id doesn't match");
        assertEquals(project.getName(), "graphviz", "The name doesn't match");
        assertEquals(project.getWebUrl(),  "https://gitlab.com/graphviz/graphviz", "The web doesn't match");
        assertNotNull(project.getCommits(), "The list of commits is null");
        assertNotNull(project.getIssues(), "The list of issues is null");

        project.prettyPrint();
    }

    // ----------------------------------------------------------------------------------------------------
    // Projects

    @Test
    @DisplayName("Get Project By Id")
    void getProjectById() throws ProjectNotFoundException {
        String id = "4207231";
        Project project = service.getProjectById(id);
        assertEquals(project.getId(), id, "The id doesn't match");
        assertEquals(project.getName(), "graphviz", "The name doesn't match");
        assertEquals(project.getWebUrl(),  "https://gitlab.com/graphviz/graphviz", "The web doesn't match");

        project.prettyPrint();
    }

}