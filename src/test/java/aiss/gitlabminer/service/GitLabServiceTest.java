package aiss.gitlabminer.service;

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
class GitLabServiceTest {

    @Autowired
    GitLabService service;

    @Test
    @DisplayName("Generate Project")
    void genProject(){
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
    void getProjectById() {
        String id = "4207231";
        Project project = service.getProjectById(id);
        assertEquals(project.getId(), id, "The id doesn't match");
        assertEquals(project.getName(), "graphviz", "The name doesn't match");
        assertEquals(project.getWebUrl(),  "https://gitlab.com/graphviz/graphviz", "The web doesn't match");
    }

    // ----------------------------------------------------------------------------------------------------
    // Commits
    @Test
    @DisplayName("Get Commits")
    void getCommits() {
        String projectId = "4207231";
        Integer sinceCommits = 50;
        Integer maxPages = 5;
        List<Commit> commits = service.getCommits(projectId, sinceCommits, maxPages);

        assertTrue(commits.size() <= 20*maxPages, "There are more pages than specified");
        assertNotNull(commits, "The list of commits is null");
        assertTrue(commits.stream().allMatch(c->!c.getId().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getTitle().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getMessage().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getAuthorName().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getAuthorEmail().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getAuthoredDate().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getCommitterName().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getCommitterEmail().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getCommittedDate().equals(null)));
        assertTrue(commits.stream().allMatch(c->!c.getWebUrl().equals(null)));

        // System.out.println(commits.size());
    }

    // ----------------------------------------------------------------------------------------------------
    // Issues
    @Test
    @DisplayName("Get Issues")
    void getIssues(){
        String projectId = "4207231";
        Integer sinceIssues = 20;
        Integer maxPages = 1;
        List<Issue> issues = service.getIssues(projectId, sinceIssues, maxPages);

        assertTrue(issues.size() <= 20*maxPages, "There are more pages than specified");
        assertNotNull(issues, "The list of issues is null");
        assertTrue(issues.stream().allMatch(i->!i.getId().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getIid().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getTitle().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getDescription().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getState().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getCreatedAt().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getAuthor().equals(null)));
        assertTrue(issues.stream().allMatch(i->i.getState().equals("closed") ? !i.getClosedAt().equals(null) : true));

        System.out.println(issues);
        System.out.println(issues.size());
    }

    // ----------------------------------------------------------------------------------------------------
    // Comments
    @Test
    @DisplayName("Get Comments")
    void getComments(){
        String projectId = "4207231";
        String issueIID = "2374";
        List<Comment> comments = service.getComments(projectId, issueIID);

        assertNotNull(comments, "The list of comments is null");
        assertTrue(comments.stream().allMatch(c->!c.getId().equals(null)));
        assertTrue(comments.stream().allMatch(c->!c.getBody().equals(null)));
        assertTrue(comments.stream().allMatch(c->!c.getAuthor().equals(null)));
        assertTrue(comments.stream().allMatch(c->!c.getCreatedAt().equals(null)));

        //System.out.println(comments.size());
        //System.out.println(comments);
    }


}