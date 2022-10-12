package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.UserBusiness;
import it.cleverad.engine.web.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@Tag(name = "User", description = "Endpoints for all the User Operations")
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create User", description = "Creates a new User")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO create(@ModelAttribute UserBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Users", description = "Lists the Users, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<UserDTO> search(UserBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Lists the Users", description = "Lists the Users by Company, searched and paginated")
    @GetMapping("/{affiliateId}/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<UserDTO> searchCompanyUsers(Long affiliateId, Pageable pageable) {
        UserBusiness.Filter request = new UserBusiness.Filter();
        request.setAffiliateId(affiliateId);
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the User", description = "Update the specific User")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO update(@PathVariable Long id, @RequestBody UserBusiness.Filter request){
        return business.update(id, request);
    }

    @Operation(summary = "Get the User", description = "Get the specific User")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Get the User ", description = "Get the specific User by Username")
    @GetMapping("/{username}/username")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getByUSername(@PathVariable String username) {
        return business.findByUsername(username);
    }

    @Operation(summary = "Delete User", description = "Delete the specific User")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }


    @Operation(summary = "Enable the User", description = "Enable the specific User")
    @PatchMapping(path = "/{id}/enable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO enable(@PathVariable Long id) throws Exception {
        return business.enableUser(id);
    }

    @Operation(summary = "Disable the User", description = "Disable the specific User")
    @PatchMapping(path = "/{id}/disable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO disable(@PathVariable Long id) throws Exception {
        return business.disableUser(id);
    }

    /**
     * ============================================================================================================
     **/

}
