package vn.hoidanit.jobhunter.controller;

import java.util.Optional;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.dto.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.anotation.APIMessage;
import vn.hoidanit.jobhunter.util.error.IdInvaliException;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @APIMessage("Create a company")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(company));
    }

    @GetMapping("/companies")
    @APIMessage("Fetch all company")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec,
            Pageable pageable) {

        return ResponseEntity.ok().body(this.companyService.handleGetAllCompanies(spec, pageable));
    }

    @PutMapping("/companies")
    @APIMessage("Update a company")
    public ResponseEntity<Company> UpdateCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.ok().body(this.companyService.handleUpdateCompany(company));
    }

    @DeleteMapping("/companies/{id}")
    @APIMessage("Delete a company by id")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/companies/{id}")
    @APIMessage("Fetch a company by id")
    public ResponseEntity<Company> fetchById(@PathVariable("id") long id) throws IdInvaliException {

        // Check id
        Optional<Company> companyOptional = this.companyService.fetchById(id);
        if (companyOptional == null) {
            throw new IdInvaliException("Resume with Id: " + id + " is not exist !");
        }
        return ResponseEntity.ok().body(companyOptional.get());
    }
}
