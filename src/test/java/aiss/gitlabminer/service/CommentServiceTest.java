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
class CommentServiceTest {

    @Autowired
    CommentService service;

    // Comments
    @Test
    @DisplayName("Get Comments")
    void getComments(){
        String projectId = "4207231";
        String issueIID = "1219";
        int maxPages = 2;
        List<Comment> comments = service.getComments(projectId, issueIID, maxPages);

        assertNotNull(comments, "The list of comments is null");
        assertTrue(comments.stream().allMatch(c->!c.getId().equals(null)));
        assertTrue(comments.stream().allMatch(c->!c.getBody().equals(null)));
        assertTrue(comments.stream().allMatch(c->!c.getAuthor().equals(null)));
        assertTrue(comments.stream().allMatch(c->!c.getCreatedAt().equals(null)));
        assertTrue(comments.size() <= 20*maxPages, "There are more comment pages than specified");

        System.out.println(comments.size());
    }


}