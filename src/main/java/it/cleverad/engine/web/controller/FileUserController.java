package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.FileUserBusiness;
import it.cleverad.engine.web.dto.FileUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping(value = "/fileuser")
public class FileUserController {

    @Autowired
    private FileUserBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadFileUserFile(@RequestParam("file") MultipartFile file, FileUserBusiness.BaseCreateRequest request) {
        return business.storeFile(file, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileUserDTO> search(FileUserBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping(path = "/user/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileUserDTO> search(@PathVariable Long affiliateId, Pageable pageable) {
        FileUserBusiness.Filter request = new FileUserBusiness.Filter();
        request.setUserId(affiliateId);
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FileUserDTO update(@PathVariable Long id, @RequestBody FileUserBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FileUserDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}/old")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteFile(@PathVariable Long id) {
        this.business.deleteFile(id);
    }

    @GetMapping("/{id}/download")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Resource> downFile(@PathVariable Long id) throws IOException {
        return business.downloadFile(id);
    }

    @PostMapping("/avatar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long storeAvatarfile(@RequestParam("file") MultipartFile file, FileUserBusiness.BaseCreateRequest request) throws IOException {
        return business.storeAvatarFile(file, request);
    }

    @GetMapping("/avatar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FileUserDTO getAvaTarfile() throws IOException {
        return business.getAvatarFile();
    }

    /**
     * ============================================================================================================
     **/

}