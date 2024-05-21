package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.FileFeedBusiness;
import it.cleverad.engine.web.dto.FileFeedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping(value = "/filefeed")
public class FileFeedController {

    @Autowired
    private FileFeedBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadFileFeed(@RequestParam("file") MultipartFile file, FileFeedBusiness.BaseCreateRequest request) {
        return business.storeFile(file, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileFeedDTO> search(FileFeedBusiness.Filter request, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping(path = "/{advertiserId}/advertiser")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileFeedDTO> searchadvertiserId(@PathVariable Long advertiserId, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        FileFeedBusiness.Filter request = new FileFeedBusiness.Filter();
        request.setAdvertiserId(advertiserId);
        return business.search(request, pageable);
    }

    @GetMapping(path = "/{feedId}/feed")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<FileFeedDTO> searchfeedId(@PathVariable Long feedId, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        FileFeedBusiness.Filter request = new FileFeedBusiness.Filter();
        request.setFeedId(feedId);
        return business.search(request, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FileFeedDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.deleteFile(id);
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