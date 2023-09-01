package it.cleverad.engine.service.telegram.app;

import it.cleverad.engine.service.telegram.app.BotConfigImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.api.auth.TLCheckedPhone;
import org.telegram.api.contact.TLContact;
import org.telegram.api.contact.TLImportedContact;
import org.telegram.api.contacts.TLAbsContacts;
import org.telegram.api.contacts.TLContacts;
import org.telegram.api.contacts.TLContactsNotModified;
import org.telegram.api.contacts.TLImportedContacts;
import org.telegram.api.engine.LoggerInterface;
import org.telegram.api.file.location.TLFileLocation;
import org.telegram.api.functions.auth.TLRequestAuthCheckPhone;
import org.telegram.api.functions.contacts.TLRequestContactsDeleteContact;
import org.telegram.api.functions.contacts.TLRequestContactsDeleteContacts;
import org.telegram.api.functions.contacts.TLRequestContactsGetContacts;
import org.telegram.api.functions.contacts.TLRequestContactsImportContacts;
import org.telegram.api.functions.upload.TLRequestUploadGetFile;
import org.telegram.api.functions.users.TLRequestUsersGetFullUser;
import org.telegram.api.input.TLInputPhoneContact;
import org.telegram.api.input.filelocation.TLInputFileLocation;
import org.telegram.api.input.user.TLAbsInputUser;
import org.telegram.api.input.user.TLInputUser;
import org.telegram.api.upload.file.TLFile;
import org.telegram.api.user.TLUser;
import org.telegram.api.user.TLUserFull;
import org.telegram.api.user.profile.photo.TLUserProfilePhoto;
import org.telegram.bot.kernel.KernelAuth;
import org.telegram.bot.kernel.KernelComm;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.mtproto.log.LogInterface;
import org.telegram.tl.TLBool;
import org.telegram.tl.TLBytes;
import org.telegram.tl.TLVector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;

@Slf4j
@Service
public class TelegramClientWrapper {

    private KernelComm kernel;

    static final int APIKEY = 25331012; // YOUR API KEY
    static final String APIHASH = "04ddf5f6a3c510966d075eee1358b193"; // YOUR API HASH
    static final String PHONENUMBER = "+393925406853"; // YOUR PHONE NUMBER
    static final long SLEEPTIME_BETWEEN_REQUESTS = 200; // ms

    public static final String PATH = new File(System.getProperty("java.io.tmpdir"),"photos").toString();
    static final SecureRandom random = new SecureRandom();

//    static {
//     //   configurateLogging();
//       // log.setLevel(Level.SEVERE);
//        new File(PATH).mkdir();
//    }
//    public TelegramClientWrapper() throws TelegramLoginException {
//
//        log.warn("TelegramClientWrapper ATTIVAZIONE!!!!");
//
//        // initialize library
//        BotConfig config = new BotConfigImpl(PHONENUMBER);
//        MemoryApiState apiState = new MemoryApiState(PHONENUMBER + ".auth");
//
//        kernel = new KernelComm(APIKEY, apiState);
//        KernelAuth kernelAuth = new KernelAuth(apiState, config, kernel, APIKEY, APIHASH);
//        kernel.init();
//
//        // and disable BotLogger
//        BotLogger.setLevel(Level.OFF);
//
//        // authorization
//       // LoginStatus status = kernelAuth.start();
//
////        if (status == LoginStatus.ERRORSENDINGCODE) {
////            throw new TelegramLoginException("Failed to send code (anti-flood?)");
////        }
//        LoginStatus status = null;
//        try {
////            if (status == LoginStatus.CODESENT) {
//                log.info("Enter code sent to " + PHONENUMBER );
//                boolean success = kernelAuth.setAuthCode("28231");
//                if (success) {
//                    status = LoginStatus.ALREADYLOGGED;
//                }
////            }
//        }catch (NoSuchElementException ex){
//            log.warn(ex.getMessage());
//        }
//
//        if (status != LoginStatus.ALREADYLOGGED) {
//           log.warn("Failed to log in: " + status);
//        }else {
//            log.warn("Successfully logged in.");
//        }
//
//    }
//
//    public ContactInfo getInfoByPhone(String phone) throws InterruptedException {
//        ContactInfo returnValue = new ContactInfo();
//
//        if (isPhoneRegistered(phone)) {
//            returnValue.setRegistered(true);
//
//            int userId = addOneContact(phone);
//            Thread.sleep(SLEEPTIME_BETWEEN_REQUESTS);
//
//            long access_hash = getAccessHash(userId);
//            Thread.sleep(SLEEPTIME_BETWEEN_REQUESTS);
//
//            deleteOneContact(userId);
//            Thread.sleep(SLEEPTIME_BETWEEN_REQUESTS);
//
//            getForeignContactInfo(userId, access_hash, returnValue);
//        } else {
//            returnValue.setRegistered(false);
//        }
//
//        returnValue.setPhone(phone);
//
//        return returnValue;
//    }
//
//    boolean isPhoneRegistered(String phone) {
//
//        boolean returnValue = false;
//
//        TLRequestAuthCheckPhone request = new TLRequestAuthCheckPhone();
//        request.setPhoneNumber(phone);
//        try {
//            TLCheckedPhone result = kernel.doRpcCallSync(request);
//            log.info("isPhoneRegistered: " + phone + " is " + result.isPhoneRegistered());
//            returnValue = result.isPhoneRegistered();
//        } catch (Exception e) {
//            log.warn("isPhoneRegistered: NOT SUCCESSFULLY");
//            log.warn(e.getMessage());
//            e.printStackTrace();
//        }
//
//        return returnValue;
//    }
//
//    void clearContactList() {
//        List<Integer> contactList = getContactList();
//        deleteContactList(contactList);
//    }
//
//    Integer addOneContact(String phone) {
//        List<String> phonelist = new ArrayList<>();
//        phonelist.add(phone);
//        List<Integer> result = addContactList(phonelist);
//        return result.get(0);
//    }
//
//    void deleteOneContact(int userId) {
//        TLInputUser user = new TLInputUser();
//        user.setUserId(userId);
//
//        TLRequestContactsDeleteContact request = new TLRequestContactsDeleteContact();
//        request.setId(user);
//
//        try {
//            kernel.doRpcCallSync(request);
//            log.info("requestDeleteContact: success");
//        } catch (Exception e) {
//            log.warn("requestDeleteContact: NOT SUCCESSFULLY");
//            log.warn(e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    List<Integer> addContactList(List<String> phonelist) {
//
//        List<Integer> returnValue = new ArrayList<>();
//
//        TLVector<TLInputPhoneContact> list = new TLVector<>();
//
//        for (String phone: phonelist) {
//            TLInputPhoneContact contact = new TLInputPhoneContact();
//            contact.setClientId(Math.abs(random.nextLong()));
//            contact.setPhone(phone);
//            contact.setFirstName(String.valueOf(random.nextLong()));
//            contact.setLastName(phone);
//
//            list.add(contact);
//        }
//
//        TLRequestContactsImportContacts request = new TLRequestContactsImportContacts();
//
//        request.setContacts(list);
//        request.setReplace(true);
//
//        try {
//            TLImportedContacts result = kernel.doRpcCallSync(request);
//
//            log.info("addContactList: success: " + result.getImported().size());
//            log.warn("addContactList: not success: " + result.getRetryContacts().size());
//
//            TLVector<TLImportedContact> listresult = result.getImported();
//            for(TLImportedContact contact : listresult) {
//                log.info("addContactList: clientId=" + contact.getClientId());
//                log.info("addContactList: userId=" + contact.getUserId());
//                log.info("");
//
//                returnValue.add(contact.getUserId());
//            }
//
//        } catch (Exception e) {
//            log.warn("addContactList: NOT SUCCESSFULLY");
//            log.warn(e.getMessage());
//            e.printStackTrace();
//        }
//
//        return returnValue;
//    }
//
//    List<Integer> getContactList() {
//
//        List<Integer> returnValue = new ArrayList<>();
//
//        TLRequestContactsGetContacts request = new TLRequestContactsGetContacts();
//        request.setHash("");
//        try {
//            TLAbsContacts result = kernel.doRpcCallSync(request);
//
//            if (result instanceof TLContactsNotModified) {
//                System.out.println("getContactList is success: NOT MODIFIED");
//            }
//            if (result instanceof TLContacts) {
//                TLContacts res = (TLContacts) result;
//                TLVector<TLContact> list = res.getContacts();
//                for (TLContact contact : list) {
//                    log.info("getContactList: userId=" + contact.getUserId());
//                    log.info("getContactList: mutual=" + contact.getMutual());
//                    log.info("");
//
//                    returnValue.add(contact.getUserId());
//                }
//            }
//
//        } catch (Exception e) {
//            log.warn("getContactList: NOT SUCCESSFULLY");
//            log.warn(e.getMessage());
//            e.printStackTrace();
//        }
//
//        return returnValue;
//    }
//
//    void deleteContactList(List<Integer> userIds) {
//
//        TLVector<TLAbsInputUser> list = new TLVector<>();
//        for (int userId : userIds) {
//            TLInputUser user = new TLInputUser();
//            user.setUserId(userId);
//            list.add(user);
//        }
//
//        TLRequestContactsDeleteContacts request = new TLRequestContactsDeleteContacts();
//        request.setId(list);
//
//        try {
//            TLBool result = kernel.doRpcCallSync(request);
//            log.info("deleteContactList: success: " + result);
//        } catch (Exception e) {
//            log.warn("deleteContactList: NOT SUCCESSFULLY");
//            log.warn(e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    void downloadPhoto(TLUserProfilePhoto photo, String outputFilename) {
//        final int bytesPerRequest = 128 * 1024;
//
//        TLInputFileLocation location = new TLInputFileLocation((TLFileLocation) photo.getPhotoBig());
//
//        try {
//            FileOutputStream file = new FileOutputStream(outputFilename);
//
//            int bytesReceivedTotal = 0;
//
//            int bytesReceivedByLastRequest = 0;
//
//            do {
//                TLRequestUploadGetFile request = new TLRequestUploadGetFile();
//                request.setLocation(location);
//                request.setLimit(bytesPerRequest);
//                request.setOffset(bytesReceivedTotal);
//
//                TLFile result = (TLFile) kernel.doRpcCallSync(request);
//                TLBytes bytes = result.getBytes();
//
//                bytesReceivedByLastRequest = bytes.getLength();
//                bytesReceivedTotal += bytesReceivedByLastRequest;
//
//                file.write(bytes.getData());
//                file.flush();
//
//            } while (bytesReceivedByLastRequest == bytesPerRequest);
//
//            file.close();
//
//        } catch (Exception e) {
//            log.warn("downloadPhoto: NOT SUCCESSFULLY");
//            log.warn(e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    void getForeignContactInfo(int userId, long access_hash, ContactInfo contactToFill) {
//        TLRequestUsersGetFullUser request = new TLRequestUsersGetFullUser();
//
//        TLInputUser user = new TLInputUser();
//        user.setUserId(userId);
//        user.setAccessHash(access_hash);
//
//        request.setId(user);
//
//        try {
//            TLUserFull result = kernel.doRpcCallSync(request);
//
//            TLUser userresult = (TLUser) result.getUser();
//            log.info("getForeignContactInfo: phone: " + userresult.getPhone());
//            log.info("getForeignContactInfo: first: " + userresult.getFirstName());
//            log.info("getForeignContactInfo: last: " + userresult.getLastName());
//            log.info("getForeignContactInfo: username: " + userresult.getUserName());
//
//            contactToFill.setUserId(userId);
//            contactToFill.setPhone(userresult.getPhone());
//            contactToFill.setFirstname(userresult.getFirstName());
//            contactToFill.setLastname(userresult.getLastName());
//            contactToFill.setUsername(userresult.getUserName());
//
//            if (userresult.getPhoto() != null) {
//                TLUserProfilePhoto photo = (TLUserProfilePhoto) userresult.getPhoto();
//
//                String outputPhotoFilename = new File(PATH, userId + ".png").toString();
//
//                Thread.sleep(SLEEPTIME_BETWEEN_REQUESTS);
//                downloadPhoto(photo, outputPhotoFilename);
//
//                contactToFill.setPhotofilename(outputPhotoFilename);
//
//                log.info("getForeignContactInfo: photo: DOWNLOADED " + outputPhotoFilename);
//            } else {
//                log.info("getForeignContactInfo: photo: NO PHOTO");
//            }
//
//        } catch (Exception e) {
//            log.warn("getForeignContactInfo: NOT SUCCESSFULLY");
//            log.warn(e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    long getAccessHash(int userId) {
//        long returnValue = -1;
//
//        TLRequestUsersGetFullUser request = new TLRequestUsersGetFullUser();
//
//        TLInputUser user = new TLInputUser();
//        user.setUserId(userId);
//
//        request.setId(user);
//
//        try {
//            TLUserFull result = kernel.doRpcCallSync(request);
//
//            TLUser userresult = (TLUser) result.getUser();
//            log.info("getAccessHash: phone: " + userresult.getPhone());
//            log.info("getAccessHash: first: " + userresult.getFirstName());
//            log.info("getAccessHash: last: " + userresult.getLastName());
//            log.info("getAccessHash: username: " + userresult.getUserName());
//            log.info("getAccessHash: access_hash: " + userresult.getAccessHash());
//
//            returnValue = userresult.getAccessHash();
//
//        } catch (Exception e) {
//            log.warn("getAccessHash: NOT SUCCESSFULLY");
//            log.warn(e.getMessage());
//            e.printStackTrace();
//        }
//
//        return returnValue;
//    }

}