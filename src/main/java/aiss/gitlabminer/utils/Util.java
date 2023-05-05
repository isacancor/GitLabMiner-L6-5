package aiss.gitlabminer.utils;

import aiss.gitlabminer.model.Issue;
import aiss.gitlabminer.model.Issue2;
import org.springframework.http.HttpHeaders;

public class Util {

    public static String getNextPageUrl(HttpHeaders headers) {
        if (headers.getFirst("X-Next-Page").equals("")) {
            return null;
        }

        return headers.getFirst(HttpHeaders.LINK)
                .split(";")[0]
                .replaceAll("<|>", "")
                .replaceAll(" rel=\"next\"", "");
    }

    public static Issue parseIssue(Issue2 oldIssue){
        Issue newIssue = new Issue();
        newIssue.setId(oldIssue.getId());
        newIssue.setTitle(oldIssue.getTitle());
        newIssue.setDescription(oldIssue.getDescription());
        newIssue.setState(oldIssue.getState());
        newIssue.setCreatedAt(oldIssue.getCreatedAt());
        newIssue.setUpdatedAt(oldIssue.getUpdatedAt());
        newIssue.setClosedAt(oldIssue.getClosedAt());
        newIssue.setLabels(oldIssue.getLabels());
        newIssue.setAuthor(oldIssue.getAuthor());
        newIssue.setAssignee(oldIssue.getAssignee());
        newIssue.setUpvotes(oldIssue.getUpvotes());
        newIssue.setDownvotes(oldIssue.getDownvotes());
        newIssue.setWebUrl(oldIssue.getWebUrl());

        newIssue.setRefId(oldIssue.getIid());

        return newIssue;
    }

}
