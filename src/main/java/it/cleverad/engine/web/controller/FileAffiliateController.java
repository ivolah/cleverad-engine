package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.FileAffiliateBusiness;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.FileAffiliateDTO;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping(value = "/fileaffiliate")
public class FileAffiliateController {

    @Autowired
    private FileAffiliateBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadFileAffiliate(@RequestParam("file") MultipartFile file, FileAffiliateBusiness.BaseCreateRequest request) {
        try {
            return business.store(file, request);
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!");
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileAffiliateDTO> search(FileAffiliateBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping(path = "/affiliate/{affiliateId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileAffiliateDTO> search(@PathVariable Long affiliateId, Pageable pageable) {
        FileAffiliateBusiness.Filter request = new FileAffiliateBusiness.Filter();
        request.setAffiliateId(affiliateId);
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FileAffiliateDTO update(@PathVariable Long id, @RequestBody FileAffiliateBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FileAffiliateDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getTypes() {
        return business.getTypes();
    }

    /**
     * ============================================================================================================
     **/

}
