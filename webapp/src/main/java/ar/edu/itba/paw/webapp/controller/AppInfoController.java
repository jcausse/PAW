package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PropertySource(value = "classpath:git.properties", ignoreResourceNotFound = true)
public class AppInfoController {

    @Value("${git.commit.id:unknown}")
    private String commitId;

    @Value("${git.build.time:unknown}")
    private String buildTime;

    @Value("${git.branch:unknown}")
    private String branch;

    @GetMapping(value = "/appinfo", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String appInfo() {
        return String.format("Branch: %s\nCommit: %s\nBuild Time: %s", branch, commitId, buildTime);
    }
}
