package it.cleverad.engine.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileStoreService {

    public String storeFile(Long id, String tipo, String fileName, byte[] bytes) throws IOException {
        String filePath = Paths.get("/media/cleverad", String.valueOf(id), tipo, fileName).toString();
        FileUtils.writeByteArrayToFile(new File(filePath), bytes);
        return filePath;
    }

    public String storeFileNew(Long id, String tipo, String fileName, byte[] bytes) throws IOException {
        String filePath = Paths.get("/media/cleverad", tipo, String.valueOf(id), fileName).toString();
        FileUtils.writeByteArrayToFile(new File(filePath), bytes);
        return filePath;
    }

    public byte[] retrieveFile(String filePath) throws IOException {
        File daScaricare = new File(filePath);
        byte[] bytes = null;
        try {
            bytes = FileUtils.readFileToByteArray(daScaricare);
        } catch (FileNotFoundException exception) {
            log.trace("File " + filePath + " not found.");
        }
        return bytes;
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