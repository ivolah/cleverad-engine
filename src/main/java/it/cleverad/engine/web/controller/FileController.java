package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.FileBusiness;
import it.cleverad.engine.web.dto.FileDTO;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@Tag(name = "Files", description = "Endpoints for all the Files Operations")
@RestController
@RequestMapping(value = "/file")
public class FileController {

    @Autowired
    private FileBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create File", description = "Creates a new File")
    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return business.store(file);
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!");
        }
    }

    @Operation(summary = "Lists the Files", description = "Lists the Files, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileDTO> search(FileBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the File", description = "Update the specific File")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FileDTO update(@PathVariable Long id, @RequestBody FileBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the File", description = "Get the specific File")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FileDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete File", description = "Delete the specific File")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/encoded")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<FileDTO> listaFileCodificati() {
        return business.listaFileCodificati();
    }


    /**
     * ============================================================================================================
     **/

}
