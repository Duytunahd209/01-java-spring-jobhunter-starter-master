package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.domain.dto.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.anotation.APIMessage;
import vn.hoidanit.jobhunter.util.error.IdInvaliException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final JobService jobService;

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService, JobService jobService) {
        this.resumeService = resumeService;
        this.jobService = jobService;
    }

    @PostMapping("/resumes")
    @APIMessage("Create a new resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvaliException {
        // check user and job
        boolean isExistID = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isExistID) {
            throw new IdInvaliException("User ID/Job ID is not exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @APIMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvaliException {

        Optional<Resume> resumeOpt = this.resumeService.fetchByID(resume.getId());
        if (resumeOpt.isEmpty()) {
            throw new IdInvaliException("Resume with id :" + resume.getId() + " is not exist!");
        }
        Resume currentResume = resumeOpt.get();
        currentResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.update(currentResume));
    }

    @DeleteMapping("/resumes/{id}")
    @APIMessage("Delete a resume")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvaliException {
        // Check id
        Optional<Resume> resumeOptional = this.resumeService.fetchByID(id);
        if (resumeOptional == null) {
            throw new IdInvaliException("Resume with Id: " + id + " is not exist !");
        }

        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @APIMessage("Fetch resume by id")
    public ResponseEntity<ResFetchResumeDTO> fetchById(@PathVariable("id") long id) throws IdInvaliException {

        // Check id
        Optional<Resume> resumeOptional = this.resumeService.fetchByID(id);
        if (resumeOptional == null) {
            throw new IdInvaliException("Resume with Id: " + id + " is not exist !");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @APIMessage("Fetch all resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchAll(spec, pageable));
    }
}
