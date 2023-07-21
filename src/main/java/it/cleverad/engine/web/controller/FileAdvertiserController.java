package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.FileAdvertiserBusiness;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.FileAdvertiserDTO;
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
@RequestMapping(value = "/fileadvertiser")
public class FileAdvertiserController {

    @Autowired
    private FileAdvertiserBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadFileAdvertiser(@RequestParam("file") MultipartFile file, FileAdvertiserBusiness.BaseCreateRequest request) {
        return business.storeFile(file, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileAdvertiserDTO> search(FileAdvertiserBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping(path = "/advertiser/{advertiserId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileAdvertiserDTO> search(@PathVariable Long advertiserId, Pageable pageable) {
        FileAdvertiserBusiness.Filter request = new FileAdvertiserBusiness.Filter();
        request.setAdvertiserId(advertiserId);
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FileAdvertiserDTO update(@PathVariable Long id, @RequestBody FileAdvertiserBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FileAdvertiserDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.deleteFile(id);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
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
