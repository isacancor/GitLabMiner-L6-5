package aiss.gitlabminer.controller;

import aiss.gitlabminer.model.Project;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class GitLabController {

    @GetMapping
    public List<Project> getAll() {
        return null;
    }

}
