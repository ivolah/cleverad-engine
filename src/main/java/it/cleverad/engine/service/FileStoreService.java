package it.cleverad.engine.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class FileStoreService {

    public String storeFile(Long affiliateId, String fileName, byte[] bytes) throws IOException {
        String filePath = "/media/cleverad/" + affiliateId + "/" + fileName;

        FileUtils.writeByteArrayToFile(new File(filePath), bytes);

        return filePath;
    }

    public byte[] retrieveFile(String filePath) throws IOException {
        File daScaricare = new File(filePath);
        return FileUtils.readFileToByteArray(daScaricare);
    }

    public Boolean deleteFile(String filePath) {
        File daCancellare = new File(filePath);
        if (daCancellare.exists()) {
            daCancellare.delete();
            daCancellare.deleteOnExit();
            return true;
        } else {
            return false;
        }
    }


}