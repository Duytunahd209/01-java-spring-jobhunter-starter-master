package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.dto.Skill;
import vn.hoidanit.jobhunter.domain.dto.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.anotation.APIMessage;
import vn.hoidanit.jobhunter.util.error.IdInvaliException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @APIMessage("Create a new job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job)
            throws IdInvaliException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobService.handleCreateJob(job));
    }

    @PutMapping("/jobs")
    @APIMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job) throws IdInvaliException {

        Optional<Job> currentJob = this.jobService.fetchJobById(job.getId());
        if (!currentJob.isPresent()) {
            throw new IdInvaliException("Job not found !");
        }
        return ResponseEntity.ok().body(this.jobService.handleUpdateJob(job, currentJob.get()));
    }

    @DeleteMapping("/jobs/{id}")
    @APIMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") Long id) throws IdInvaliException {

        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvaliException("Job not found !");
        }

        this.jobService.deleteJob(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @APIMessage("Get a job by id")
    public ResponseEntity<Job> getJobById(@PathVariable("id") Long id) throws IdInvaliException {

        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvaliException("Job not found !");
        }
        return ResponseEntity.ok(currentJob.get());
    }

    @GetMapping("/jobs")
    @APIMessage("Fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.handleGetAllJob(spec, pageable));
    }

}
