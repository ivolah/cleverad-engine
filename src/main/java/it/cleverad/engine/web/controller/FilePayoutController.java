package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.FilePayoutBusiness;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.FilePayoutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping(value = "/filepayout")
public class FilePayoutController {

    @Autowired
    private FilePayoutBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadFilePayout(@RequestParam("file") MultipartFile file, FilePayoutBusiness.BaseCreateRequest request) {
        return business.storeFile(file, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FilePayoutDTO> search(FilePayoutBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping(path = "/{payoutId}/payout")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FilePayoutDTO> search(@PathVariable Long payoutId, Pageable pageable) {
        FilePayoutBusiness.Filter request = new FilePayoutBusiness.Filter();
        request.setPayoutId(payoutId);
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FilePayoutDTO update(@PathVariable Long id, @RequestBody FilePayoutBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FilePayoutDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.deleteFile(id);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public Page<DictionaryDTO> getTypes() {
        return business.getTypes();
    }

    @GetMapping("/{id}/download")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Resource> down(@PathVariable Long id) {
        return business.downloadFile(id);
    }

    /**
     * ============================================================================================================
     **/

}
