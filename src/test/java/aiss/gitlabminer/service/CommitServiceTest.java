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
class CommitServiceTest {

    @Autowired
    CommitService service;

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


}