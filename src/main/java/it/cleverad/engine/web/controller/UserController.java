package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.UserBusiness;
import it.cleverad.engine.web.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO create(@ModelAttribute UserBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<UserDTO> search(UserBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping("/{affiliateId}/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<UserDTO> searchAffilaiteUsers(@PathVariable Long affiliateId, Pageable pageable) {
        return business.searchByAffiliateID(affiliateId, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO update(@PathVariable Long id, @RequestBody UserBusiness.Filter request){
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @GetMapping("/{username}/username")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getByUSername(@PathVariable String username) {
        return business.findByUsername(username);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }


    @PatchMapping(path = "/{id}/enable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO enable(@PathVariable Long id) throws Exception {
        return business.enableUser(id);
    }

    @PatchMapping(path = "/{id}/disable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO disable(@PathVariable Long id) throws Exception {
        return business.disableUser(id);
    }

    @PatchMapping(path = "/{id}/reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO reset(@PathVariable Long id, @RequestBody UserBusiness.Confirm request) throws Exception {
        return business.resetPassword(id, request.getPassword());
    }

    /**
     * ============================================================================================================
     **/

}
