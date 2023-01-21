package com.zbro.messydoc.worker;


import com.zbro.messydoc.worker.document.DocContentProcessor;
import com.zbro.messydoc.commons.document.DocumentEntity;
import com.zbro.messydoc.worker.document.DocumentProcessor;
import com.zbro.messydoc.commons.event.MessyDocEvent;
import com.zbro.messydoc.worker.file.*;
import com.zbro.messydoc.worker.reader.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class AlPhaDogTask implements Runnable {

    private DocContentProcessor docContentProcessor;
    private WorkerEntitySingleton workerProfile;

    private Set<String> path;

    public void setPath(Set<String> path){
        this.path = path;
    }

    @Autowired
    ApplicationEventPublisher eventPublisher;

    public AlPhaDogTask(@Autowired DocContentProcessor docContentProcessor,
                        @Autowired WorkerEntitySingleton workerProfile){
        this.docContentProcessor = docContentProcessor;
        this.workerProfile = workerProfile;
    }

    @Override
    public void run() {
        workerProfile.setStatus("working");
        eventPublisher.publishEvent(new MessyDocEvent("update"));

        FileScanner scaner = new FileScanner();
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


        Set<FileRawEntity> totalFileSet = new HashSet<>();
        Set<FileRawEntity> oldSet = new HashSet<>();
        for (String path: path.toArray(new String[0])){

            // Use "[path]" to do the HashDigest
            String formatPath =  Arrays.toString(new String[]{path});

            //1. check the path, load the old version rawFileSet if exist
            try{
                if(Files.exists((Paths.get(("fileDb" + "-" + MD5Hash.digest(formatPath)))))){
                log.info("file exist in the path:{}, load file",path);
                oldSet = (Set<FileRawEntity>)fileDataStorage.loadFromDbFile(("fileDb" + "-" + MD5Hash.digest(formatPath)));
                }
            }catch (Exception e){
                log.error(e.toString());
            }

            long t1 = System.currentTimeMillis();
            //2. scan the path, initiate the new rawFileSet
            Set<FileRawEntity> newSet = scaner.scan(path);
            log.info("scan path {} complete, {} files found, cost: {}ms",path,newSet.size(),System.currentTimeMillis() - t1);

            //3. save new rawFileSet to path
            String dbFile = fileDataStorage.saveToDbFile(newSet,path);
            log.info("save db file to {}",dbFile);
            //4. find new files in newSet against the oldSet
            totalFileSet.addAll(inspector.getDifferentInNewSet(oldSet,newSet));
            //5.find deleted files and mark it in db
            // mark the deleted files with version
        }
        log.info("scan and compare files complete, {} files need to be processed", totalFileSet.size());


        log.info("aggregate files begin");
        long t2 = System.currentTimeMillis();

        //6.aggregate the files , from rawType to final DocType, set some fields
        //fill the version
        //merge same files
        Set<DocumentEntity> files = aggregator.aggregateDoc(totalFileSet);
        //sets the Hostname
        String hostName = workerProfile.getName() + "-" + workerProfile.getAdvertisedListener();
        files.forEach(e->e.setHostName(hostName));

        log.info("aggregation cost: {}ms", System.currentTimeMillis() - t2);
        log.info("aggregate complete {} files need to sniff content and persist", files.size());

        log.info("sniff content and persist to db begin");

        long t3 = System.currentTimeMillis();
        //7. read and fill contents then persist to db
        docContentProcessor.readFileContentAndSaveToEs(files);
        log.info("sniff content and persist to db cost: {}ms", System.currentTimeMillis() - t3);

        workerProfile.setStatus("ready to work");
        eventPublisher.publishEvent(new MessyDocEvent("update"));

    }
}
