package it.cleverad.engine.web.controller.incoming;
//
//import it.cleverad.engine.business.AdvertiserBusiness;
//import it.cleverad.engine.business.RepresentativeBusiness;
//import it.cleverad.engine.business.incoming.RegistrazioneBusiness;
//import it.cleverad.engine.web.dto.AdvertiserDTO;
//import it.cleverad.engine.web.dto.RegistrazioneDTO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//
//@CrossOrigin
//@RestController
//@RequestMapping(value = "/registrazione")
//public class RegistrazioneController {
//
//    @Autowired
//    private RegistrazioneBusiness business;
//
//    /**
//     * ============================================================================================================
//     **/
//
//
//    @GetMapping
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public Page<RegistrazioneDTO> search(RegistrazioneBusiness.Filter request) {
//        return business.search(request);
//    }
//
//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public RegistrazioneDTO getByUuid(@PathVariable Long id) throws Exception {
//        return business.findById(id);
//    }
//
//
//    /**
//     * ============================================================================================================
//     **/
//
//}