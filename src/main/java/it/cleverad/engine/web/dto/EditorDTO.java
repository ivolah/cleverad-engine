package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Editor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EditorDTO {

    private long id;
    private String name;
    private String vatNumber;
    private String street;
    private String streetNumber;
    private String city;
    private String province;
    private String zipCode;
    private String primaryMail;
    private String secondaryMail;
    private String country;
    private String phonePrefix;
    private String phoneNumber;

    private String note;

    private String bank;
    private String iban;
    private String swift;
    private String paypal;

    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public EditorDTO(long id, String name, String vatNumber, String street, String streetNumber, String city, String province, String zipCode, String primaryMail, String secondaryMail, String country, String phonePrefix, String phoneNumber, String note, String bank, String iban, String swift, String paypal, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.name = name;
        this.vatNumber = vatNumber;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
        this.primaryMail = primaryMail;
        this.secondaryMail = secondaryMail;
        this.country = country;
        this.phonePrefix = phonePrefix;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.bank = bank;
        this.iban = iban;
        this.swift = swift;
        this.paypal = paypal;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static EditorDTO from(Editor editor) {
        return new EditorDTO(editor.getId(), editor.getName(), editor.getVatNumber(), editor.getStreet(), editor.getStreetNumber(),
                editor.getCity(), editor.getProvince(), editor.getZipCode(), editor.getPrimaryMail(), editor.getSecondaryMail(),
                editor.getCountry(), editor.getPhonePrefix(), editor.getPhoneNumber(),
                editor.getNote(), editor.getBank(), editor.getIban(), editor.getSwift(), editor.getPaypal(),
                editor.getStatus(), editor.getCreationDate(), editor.getLastModificationDate());
    }

}
