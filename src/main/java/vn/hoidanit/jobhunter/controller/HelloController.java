package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.util.error.IdInvaliException;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() throws IdInvaliException {
        return "Hello World (Hỏi Dân IT & Eric)";
    }
}
