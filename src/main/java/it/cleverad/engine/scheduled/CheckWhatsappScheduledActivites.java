package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.persistence.repository.service.TransactionCPLRepository;
import it.cleverad.engine.service.whatsapp.WhatsappService;
import it.cleverad.engine.web.dto.TransactionCPLDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Async
public class CheckWhatsappScheduledActivites {

    @Autowired
    WhatsappService whatsappService;

    @Autowired
    TransactionCPLRepository transactionCPLRepository;
    @Autowired
    TransactionBusiness transactionBusiness;

    //  @Scheduled(cron = "22 20 0/4 * * ?")
    protected void verificaWhatsapp() {

        // trovo tutte le campagne da analizzare


        // trovo tutti gli cpl con stato telefono vuoto
        TransactionBusiness.Filter requestDataStatus = new TransactionBusiness.Filter();
        requestDataStatus.setPhoneVerifiedNull(true);
        requestDataStatus.setCampaignId(232L);
        Page<TransactionCPLDTO> listaCplStatus = transactionBusiness.searchCpl(requestDataStatus, Pageable.ofSize(Integer.MAX_VALUE));

        listaCplStatus.forEach(transactionCPLDTO -> {
            log.info("CCCC >>>> {}", transactionCPLDTO.getId());
            // leggo numero da DB Luca
//            RegistrazioneBusiness.Filter regReq = new RegistrazioneBusiness.Filter();
////            regReq.setUserAgent(transactionCPLDTO.getAgent());
////            regReq.setIp(transactionCPLDTO.getIp());
//            regReq.setIp("176.201.41.233");
//            Page<RegistrazioneDTO> regis = registrazioneBusiness.search(regReq);
//
//            RegistrazioneDTO rr = regis.stream().findFirst().get();
//            String numero = rr.getTelefono();

            // >>>>>>>>> verifico
//            Boolean status = false;
//            try {
//                status = whatsappService.checkNumber(numero);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

            // >>>>>>>>>> aggiorno dato in DB cleverad
//            transactionBusiness.updateCPLPhoneNumber(numero, transactionCPLDTO.getId());
//            transactionBusiness.updatePhoneStatus(transactionCPLDTO.getId(), numero, status);
            // transactionBusiness.updateCPLPhoneStatus(status, transactionCPLDTO.getId());

            // cerco per order ID
            //TransactionBusiness.Filter requestData = new TransactionBusiness.Filter();
            //requestData.setData(data);
            //Page<TransactionCPLDTO> listaCpl = transactionBusiness.searchCpl(requestData);
            //
            //if (listaCpl.getTotalElements() == 1) {
            //TransactionCPLDTO transactionCPLDTO = listaCpl.stream().findFirst().get();
            //transactionBusiness.updateCPLPhoneNumber(numero, transactionCPLDTO.getId());
            //transactionBusiness.updateCPLPhoneStatus(status, transactionCPLDTO.getId());
            //}

        });

    }


}