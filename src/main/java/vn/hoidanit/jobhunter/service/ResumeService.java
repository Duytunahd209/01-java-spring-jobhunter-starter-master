package vn.hoidanit.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.print.attribute.standard.JobImpressionsSupported;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Skill;
import vn.hoidanit.jobhunter.domain.dto.Response.ResCreateUserDTO.CompanyUser;
import vn.hoidanit.jobhunter.domain.dto.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResFetchResumeDTO.JobResume;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResFetchResumeDTO.UserResume;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;
import vn.hoidanit.jobhunter.util.constant.ResumeStateEnum;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {

        // check user by id
        if (resume.getUser() == null) {
            return false;
        }
        Optional<User> userOpt = this.userRepository.findById(resume.getUser().getId());
        if (userOpt.isEmpty()) {
            return false;
        }

        // Check job
        if (resume.getJob() == null) {
            return false;
        }
        Optional<Job> jobOpt = this.jobRepository.findById(resume.getJob().getId());
        if (jobOpt.isEmpty()) {
            return false;
        }

        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        resume = this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());

        return res;
    }

    public Optional<Resume> fetchByID(Long id) {
        return this.resumeRepository.findById(id);
    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }
        res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return res;
    }

    public ResultPaginationDTO fetchAll(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageSkill = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageSkill.getNumber() + 1);
        meta.setPageSize(pageSkill.getSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        List<ResFetchResumeDTO> listResume = pageSkill.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(listResume);

        return resultPaginationDTO;
    }
}
