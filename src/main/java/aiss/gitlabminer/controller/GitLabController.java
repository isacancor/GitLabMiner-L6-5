package aiss.gitlabminer.controller;

import aiss.gitlabminer.exception.ProjectNotFoundException;
import aiss.gitlabminer.model.Project;
import aiss.gitlabminer.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Tag(name = "GitLab Project", description = "GitLab Project management API")
@RestController
@RequestMapping("/gitlabminer")
public class GitLabController {

    @Autowired
    ProjectService service;

    @Autowired
    RestTemplate restTemplate;

    // GET /gitlabminer/{id}[?sinceCommits=5&sinceIssues=30&maxPages=2]
    @Operation(
            summary = "Retrieve a GitLab Project",
            description = "Get a GitLab Project by specifying some parameters",
            tags = { "project", "get" })
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Project found",
                    content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Project not found",
                    content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public Project getProject(@Parameter(description = "id of the project to be searched") @PathVariable String id,
                              @Parameter(description = "number of past days to search for commits")
                              @RequestParam(required=false) Integer sinceCommits,
                              @Parameter(description = "number of past days to search for issues")
                                  @RequestParam(required=false) Integer sinceIssues,
                              @Parameter(description = "max number of pages to search")
                                  @RequestParam(required=false) Integer maxPages)
            throws ProjectNotFoundException {

        Project res = service.genProject(id, sinceCommits, sinceIssues,maxPages);
        return res;
    }

    // POST /gitlabminer/{id}[?sinceCommits=5&sinceIssues=30&maxPages=2]
    @Operation(
            summary = "Send a GitLab Project to GitMiner",
            description = "Get a GitLab Project to GitMiner by specifying some parameters",
            tags = { "project", "post" })
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Sent project",
                    content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Project could not be sent",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",
                    description = "Project not found",
                    content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}")
    public Project sendProject(@Parameter(description = "id of the project to be searched") @PathVariable String id,
                               @Parameter(description = "number of past days to search for commits")
                               @RequestParam(required=false) Integer sinceCommits,
                               @Parameter(description = "number of past days to search for issues")
                                   @RequestParam(required=false) Integer sinceIssues,
                               @Parameter(description = "max number of pages to search")
                                   @RequestParam(required=false) Integer maxPages)
            throws ProjectNotFoundException {
        String uri = "http://localhost:8080/gitminer/projects";
        Project res = service.genProject(id, sinceCommits, sinceIssues,maxPages);

        ResponseEntity<Project> response = restTemplate
                .postForEntity(uri, res, Project.class);

        return response.getBody();
    }

}
