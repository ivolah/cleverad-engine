package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.AdvertiserBusiness;
import it.cleverad.engine.business.UserBusiness;
import it.cleverad.engine.web.dto.AdvertiserDTO;
import it.cleverad.engine.web.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/company")
public class AdvertiserController {

    @Autowired
    private AdvertiserBusiness business;

    @Autowired
    private UserBusiness userBusiness;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AdvertiserDTO create(@ModelAttribute AdvertiserBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AdvertiserDTO> search(AdvertiserBusiness.Filter request, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AdvertiserDTO update(@PathVariable Long id, @RequestBody AdvertiserBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdvertiserDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @PatchMapping(path = "/{id}/disable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AdvertiserDTO disable(@PathVariable Long id) {
        return business.disable(id);
    }

    @PatchMapping(path = "/{id}/enable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AdvertiserDTO enable(@PathVariable Long id) {
        return business.enable(id);
    }

    /**
     * ============================================================================================================
     **/


    @GetMapping("/{id}/operators")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDTO> getOperators(@PathVariable Long id, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return userBusiness.searchByAdvertiserId(id, pageable);
    }

    @PostMapping(value = "/{advertiserid}/operator", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO create(@PathVariable Long advertiserid, @ModelAttribute UserBusiness.BaseCreateRequest request) {
        request.setAdvertiserId(advertiserid);
        request.setRoleId(555L);
        request.setRole("Advertiser");
        return userBusiness.create(request);
    }

    @DeleteMapping("/{id}/operator")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteOperator(@PathVariable Long id) {
        this.userBusiness.delete(id);
    }

    @PatchMapping(path = "/{id}/operator")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO update(@PathVariable Long id, @RequestBody UserBusiness.Filter request) {
        return userBusiness.update(id, request);
    }

    @PatchMapping(path = "/{id}/operator/reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO reset(@PathVariable Long id, @RequestBody UserBusiness.Confirm request) throws Exception {
        return userBusiness.resetPassword(id, request.getPassword());
    }

    @PatchMapping(path = "/operator/reset/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO reset(@RequestBody UserBusiness.Confirm request) throws Exception {
        return userBusiness.resetPasswordUsername(request.getUsername(), request.getPassword());
    }

    @PatchMapping(path = "/operator/reset/request")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO resetRequest(@RequestBody UserBusiness.Confirm request) throws Exception {
        return userBusiness.requestResetPassword(request.getUsername(), false);
    }


    /**
     * ============================================================================================================
     **/

}