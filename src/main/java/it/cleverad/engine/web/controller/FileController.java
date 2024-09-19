package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.FileBusiness;
import it.cleverad.engine.web.dto.FileDTO;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/file")
public class FileController {

    @Autowired
    private FileBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return business.storeFile(file);
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!", e);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileDTO> search(FileBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FileDTO getByUuid(@PathVariable Long id) throws IOException {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.deleteFile(id);
    }

    @GetMapping("/encoded")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<FileDTO> listaFileCodificati() {
        return business.listaFileCodificati();
    }

    @GetMapping("/{id}/download")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Resource> down(@PathVariable Long id) {
        try {
            return business.downloadFile(id);
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore download!", e);
        }
    }

    /**
     * ============================================================================================================
     **/

}