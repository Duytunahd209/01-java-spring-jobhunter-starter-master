package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository,
            CompanyRepository companyRepository,
            CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyService = companyService;
    }

    public User handleCreateUser(User user) {
        // Check exist company
        if (user.getCompany() != null) {
            Optional<Company> optCompany = this.companyRepository.findById(
                    user.getCompany().getId());
            user.setCompany(optCompany.isPresent() ? optCompany.get() : null);
        }
        return this.userRepository.save(user);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO ConvertToResCreateDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser resCompany = new ResCreateUserDTO.CompanyUser();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        // check company
        if (user.getCompany() != null) {
            resCompany.setId(user.getCompany().getId());
            resCompany.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(resCompany);
        }

        return resCreateUserDTO;
    }

    public ResUserDTO ConvertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser resCompany = new ResUserDTO.CompanyUser();

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        // check company
        if (user.getCompany() != null) {
            resCompany.setId(user.getCompany().getId());
            resCompany.setName(user.getCompany().getName());
            resUserDTO.setCompany(resCompany);
        }

        return resUserDTO;
    }

    public ResultPaginationDTO handleGetAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageUser.getNumber() + 1);
        meta.setPageSize(pageUser.getSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        // Remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getAge(),
                        item.getGender(),
                        item.getAddress(),
                        item.getCreatedAt(),
                        item.getUpdatedAt(),
                        new ResUserDTO.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null)))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(listUser);

        return resultPaginationDTO;
    }

    public User fetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User handleUpdateUser(User userinput) {
        User currentUser = this.fetchUserById(userinput.getId());

        if (currentUser != null) {
            currentUser.setName(userinput.getName());
            currentUser.setAddress(userinput.getAddress());
            currentUser.setGender(userinput.getGender());
            currentUser.setAge(userinput.getAge());

            // Check company
            if (userinput.getCompany() != null) {
                Optional<Company> optCompany = this.companyService.findCompanyById(
                        userinput.getCompany().getId());
                currentUser.setCompany(optCompany.isPresent() ? optCompany.get() : null);

            }
            currentUser = this.userRepository.save(currentUser);
            return currentUser;
        }
        return null;
    }

    public ResUpdateUserDTO ConvertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser resCompany = new ResUpdateUserDTO.CompanyUser();

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        // check company
        if (user.getCompany() != null) {
            resCompany.setId(user.getCompany().getId());
            resCompany.setName(user.getCompany().getName());
            resUserDTO.setCompany(resCompany);
        }

        return resUserDTO;
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUserName(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}
