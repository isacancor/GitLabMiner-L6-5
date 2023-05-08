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
class IssueServiceTest {

    @Autowired
    IssueService service;

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
        assertTrue(issues.stream().allMatch(i->!i.getRefId().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getTitle().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getDescription().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getState().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getCreatedAt().equals(null)));
        assertTrue(issues.stream().allMatch(i->!i.getAuthor().equals(null)));
        assertTrue(issues.stream().allMatch(i->i.getState().equals("closed") ? !i.getClosedAt().equals(null) : true));

        //System.out.println(issues);
        //System.out.println(issues.size());
    }

}