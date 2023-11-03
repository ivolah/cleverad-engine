package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.*;
import it.cleverad.engine.web.dto.AffiliateDTO;
import it.cleverad.engine.web.dto.CategoryDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/register")
public class RegisterController {

    @Autowired
    private AffiliateBusiness business;
    @Autowired
    private UserBusiness userBusiness;
    @Autowired
    private CategoryBusiness categoryBusiness;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private BBPlatformBusiness bbPlatformBusiness;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateDTO create(@ModelAttribute AffiliateBusiness.BaseCreateRequest request) {
        request.setStatus(true);
        request.setStatusId(12L);
        return business.create(request);
    }

    @PostMapping(path = "/brandbuddies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateDTO createBrandBuddy(@ModelAttribute AffiliateBusiness.BaseCreateRequest request) {
        request.setStatus(true);
        request.setStatusId(12L);
        request.setBrandbuddies(true);
        request.setCompanytypeId(71L);
        request.setChannelTypeId(30L);
        request.setChannelOwnerId(43L);
        request.setBusinessTypeId(53L);
        request.setChannelDimension("1");
        request.setChannelUrl("");
        request.setName(request.getFirstName().toUpperCase() + " " + request.getLastName().toUpperCase());
        request.setChannelName("Canale BrandBuddies " + request.getFirstName().toUpperCase() + " " + request.getLastName().toUpperCase());

        AffiliateDTO dt = business.create(request);
        if (request.getBrandbuddiesPlatformId() != null) {
            BBPlatformBusiness.BaseCreateRequest BBPrequest = new BBPlatformBusiness.BaseCreateRequest();
            BBPrequest.setBrandbuddiesId(dt.getId());
            BBPrequest.setVerified(false);
            BBPrequest.setName(request.getBrandbuddiesPlatformName());
            BBPrequest.setPlatformId(request.getBrandbuddiesPlatformId());
            bbPlatformBusiness.create(BBPrequest);
        }

        return dt;
    }

    @GetMapping("/types/brandbuddies/platform")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getPlatformsBrandbuddies() {
        return dictionaryBusiness.getPlatfromBB();
    }

    @GetMapping("/user/{uuid}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO getUserToRegister(@PathVariable String uuid) {
        return userBusiness.getUserToRegister(uuid);
    }

    @PatchMapping(path = "/user/confirm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void confirmUser(@RequestBody UserBusiness.Confirm request) {
        userBusiness.confirm(request);
    }

    @GetMapping("/types/company")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getTypeCompany() {
        return business.getTypeCompany();
    }

    @GetMapping("/types/category")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getChannelTypeAffiliate() {
        return business.getChannelTypeAffiliate();
    }

    @GetMapping("/types/channel")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CategoryDTO> getChannlCategory() {
        Pageable pageable = PageRequest.of(0, 200, Sort.by(Sort.Order.asc("id")));
        return categoryBusiness.search(new CategoryBusiness.Filter(), pageable);
    }

    /**
     * ============================================================================================================
     **/

}