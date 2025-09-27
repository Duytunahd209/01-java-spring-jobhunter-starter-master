package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.dto.Skill;
import vn.hoidanit.jobhunter.domain.dto.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.anotation.APIMessage;
import vn.hoidanit.jobhunter.util.error.IdInvaliException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @APIMessage("Create a new skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skillInput) throws IdInvaliException {
        // check name
        if (skillInput.getName() != null && this.skillService.isNameExist(skillInput.getName())) {
            throw new IdInvaliException("Skill " + skillInput.getName() + " is exist, please input another skill !");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(skillInput));
    }

    @PutMapping("/skills")
    @APIMessage("Update a skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skillInput) throws IdInvaliException {
        // Check id
        Skill currentSkill = this.skillService.fetchSkillById(skillInput.getId());
        if (currentSkill == null) {
            throw new IdInvaliException("Skill with Id: " + skillInput.getId() + " is not exist !");
        }

        // check name
        if (skillInput.getName() != null && this.skillService.isNameExist(skillInput.getName())) {
            throw new IdInvaliException("Skill " + skillInput.getName() + " is exist, please input another skill !");
        }
        currentSkill.setName(skillInput.getName());
        return ResponseEntity.ok().body(this.skillService.handleUpdateSkill(currentSkill));
    }

    @GetMapping("/skills")
    @APIMessage("Fetch all skill")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(
            @Filter Specification<Skill> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.handleGetAllSkill(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvaliException {
        // Check id
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvaliException("Skill with Id: " + id + " is not exist !");
        }

        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);

    }

}
