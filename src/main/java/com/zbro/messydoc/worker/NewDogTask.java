package com.zbro.messydoc.worker;


import com.zbro.messydoc.commons.document.DocumentEntity;
import com.zbro.messydoc.commons.document.NewDocumentEntity;
import com.zbro.messydoc.commons.event.MessyDocEvent;
import com.zbro.messydoc.worker.document.DocContentProcessor;
import com.zbro.messydoc.worker.document.DocumentProcessor;
import com.zbro.messydoc.worker.file.*;
import com.zbro.messydoc.worker.reader.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Component
@Slf4j
public class NewDogTask implements Runnable {

    private DocContentProcessor docContentProcessor;
    private WorkerEntitySingleton workerProfile;

    private Set<String> path;

    public void setPath(Set<String> path){
        this.path = path;
    }

    @Autowired
    ApplicationEventPublisher eventPublisher;

    public NewDogTask(@Autowired DocContentProcessor docContentProcessor,
                      @Autowired WorkerEntitySingleton workerProfile){
        this.docContentProcessor = docContentProcessor;
        this.workerProfile = workerProfile;
    }

    @Override
    public void run() {
        workerProfile.setStatus("working");
        eventPublisher.publishEvent(new MessyDocEvent("update"));

        NewFileScaner scaner = new NewFileScaner();
        FileDataStorage fileDataStorage = new FileDataStorage();
        DocumentProcessor aggregator = new DocumentProcessor();
        FileSetInspector inspector = new FileSetInspector();

        docContentProcessor.fileParserRegister(new ExcelFileContentReader());
        docContentProcessor.fileParserRegister(new CSVFileContentReader());
        docContentProcessor.fileParserRegister(new TXTFileContentReader());
        docContentProcessor.fileParserRegister(new PPTFileContentReader());
        docContentProcessor.fileParserRegister(new WordFileContentReader());
        docContentProcessor.fileParserRegister(new PDFFileContentReader());
        log.info("scan and compare files begin");

        //process by different paths, it can't aggregate same files in different paths
        for (String path: path.toArray(new String[0])){
            Map<String, NewDocumentEntity> files = new HashMap<>();

            // Use "[path]" to do the HashDigest
            String formatPath =  Arrays.toString(new String[]{path});

            //1. check the path, load the old version files if exist
            try{
                if(Files.exists((Paths.get(("fileDb" + "-" + MD5Hash.digest(formatPath)))))){
                log.info("file exist in the path:{}, load file",path);
                files = (Map<String, NewDocumentEntity>)fileDataStorage.loadFromDbFile(("fileDb" + "-" + MD5Hash.digest(formatPath)));
                }
            }catch (Exception e){
                log.error(e.toString());
            }
            long t1 = System.currentTimeMillis();
            //2. scan the path, initiate the new files
            scaner.scan(files,path);
            log.info("scan path {} complete, {} files found, cost: {}ms",path,files.size(),System.currentTimeMillis() - t1);

            log.info("sniff content and persist to db begin");

            long t3 = System.currentTimeMillis();
            //7. read and fill contents then persist to db
            docContentProcessor.readFileContentAndSaveToEs(files);
            log.info("sniff content and persist to db cost: {}ms", System.currentTimeMillis() - t3);

            docContentProcessor.updatePathToEs(files);

            docContentProcessor.updateInvalidFilesToEs(files);

            docContentProcessor.removeInvalidFilesFromMap(files);

            //3. save new files to path
            String dbFile = fileDataStorage.saveToDbFile(files,path);
            log.info("save db file to {}",dbFile);
        }
        workerProfile.setStatus("ready to work");
        eventPublisher.publishEvent(new MessyDocEvent("update"));
    }
}
