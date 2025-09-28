package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role create(Role role) {
        // check permission
        if (role.getPermissions() != null) {
            // Get list Id permission
            List<Long> listId = role.getPermissions()
                    .stream().map(p -> p.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(listId);
            role.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(role);
    }

    public Role fetchById(long id) {
        Optional<Role> p = this.roleRepository.findById(id);
        if (p.isPresent()) {
            return p.get();
        }
        return null;
    }

    public Role update(Role r) {
        Role roleDB = this.fetchById(r.getId());

        // check permission
        if (r.getPermissions() != null) {
            // Get list Id permission
            List<Long> listId = r.getPermissions()
                    .stream().map(p -> p.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(listId);
            r.setPermissions(dbPermissions);
        }

        if (roleDB != null) {
            roleDB.setName(r.getName());
            roleDB.setDescription(r.getDescription());
            roleDB.setActive(r.isActive());
            roleDB.setPermissions(r.getPermissions());

            roleDB = this.roleRepository.save(roleDB);
            return roleDB;
        }
        return null;
    }

    public void delete(long id) {
        // Delete role
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageRole.getNumber() + 1);
        meta.setPageSize(pageRole.getSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        resultPaginationDTO.setResult(pageRole.getContent());

        return resultPaginationDTO;
    }
}
