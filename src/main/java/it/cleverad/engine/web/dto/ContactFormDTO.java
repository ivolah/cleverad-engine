package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.ContactForm;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ContactFormDTO {

    private long id;

    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String companyName;
    private String country;
    private String requestType;
    private String enquiry;
    private Boolean agreeMailingList;
    private Boolean agreeDataProcetction;

    private LocalDateTime creationDate;

    public ContactFormDTO(long id, String name, String surname, String email, String phoneNumber, String companyName, String country, String requestType, String enquiry, Boolean agreeMailingList, Boolean agreeDataProcetction, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.country = country;
        this.requestType = requestType;
        this.enquiry = enquiry;
        this.agreeMailingList = agreeMailingList;
        this.agreeDataProcetction = agreeDataProcetction;
        this.creationDate = creationDate;
    }

    public static ContactFormDTO from(ContactForm contactForm) {
        return new ContactFormDTO(contactForm.getId(), contactForm.getName(), contactForm.getSurname(), contactForm.getEmail(), contactForm.getPhoneNumber(), contactForm.getCompanyName(), contactForm.getCountry(), contactForm.getRequestType(), contactForm.getEnquiry(), contactForm.getAgreeMailingList(), contactForm.getAgreeDataProcetction(), contactForm.getCreationDate());
    }

}
