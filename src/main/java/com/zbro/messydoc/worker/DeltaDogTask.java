package com.zbro.messydoc.worker;


import com.zbro.messydoc.commons.document.NewDocumentEntity;
import com.zbro.messydoc.commons.event.MessyDocEvent;
import com.zbro.messydoc.worker.document.DocContentProcessor;
import com.zbro.messydoc.worker.file.FileDataStorage;
import com.zbro.messydoc.worker.file.NewFileScanner;
import com.zbro.messydoc.worker.reader.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class DeltaDogTask implements Runnable {

    private DocContentProcessor docContentProcessor;
    private WorkerEntitySingleton workerProfile;
    private ApplicationEventPublisher eventPublisher;

    private Set<String> sniffPaths;

    public void setSniffPaths(Set<String> sniffPaths) {
        this.sniffPaths = sniffPaths;
    }

    @Autowired
    public DeltaDogTask(DocContentProcessor docContentProcessor,
                        WorkerEntitySingleton workerProfile,
                        ApplicationEventPublisher eventPublisher) {
        this.docContentProcessor = docContentProcessor;
        this.workerProfile = workerProfile;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void run() {
        workerProfile.setStatus("working");
        eventPublisher.publishEvent(new MessyDocEvent("update"));

        NewFileScanner scanner = new NewFileScanner();
        FileDataStorage fileDataStorage = new FileDataStorage();

        docContentProcessor.fileParserRegister(new ExcelFileContentReader());
        docContentProcessor.fileParserRegister(new CSVFileContentReader());
        docContentProcessor.fileParserRegister(new TXTFileContentReader());
        docContentProcessor.fileParserRegister(new PPTFileContentReader());
        docContentProcessor.fileParserRegister(new WordFileContentReader());
        docContentProcessor.fileParserRegister(new PDFFileContentReader());
        log.info("let the dog out");


        //1. load all dbfile
        final Map<String, NewDocumentEntity> allFiles = new HashMap<>();
        try {
            Files.list(Paths.get(""))
                    .filter(e -> e.getFileName().toString().matches("fileDb.*"))
                    .forEach(e -> {
                        allFiles.putAll((Map<String, NewDocumentEntity>) fileDataStorage.loadFromDbFile(e));
                        log.info("load dbfile {} in to program", e);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //invalid the old files version
        //it will cause trouble when we scan a small set of the document, the file not scanned will be invalid
        //so this feature has to be canceled for now
//        allFiles.values().forEach(e-> e.setVersion(0));

        //2. scan the paths, initiate the new files
        for (String path : sniffPaths.toArray(new String[0])) {
            log.info("sniff the path: {} begin",path);

            long t1 = System.currentTimeMillis();

            scanner.scan(allFiles, path);
            log.info("sniff path {} complete, {} files found, cost: {}ms", path, allFiles.size(), System.currentTimeMillis() - t1);

        }
        log.info("read content and persist to db begin");
        long t3 = System.currentTimeMillis();
        //3. read and fill contents then persist to db
        docContentProcessor.readFileContentAndSaveToEs(allFiles);
        log.info("read content and persist to db cost: {}ms", System.currentTimeMillis() - t3);

        docContentProcessor.updatePathToEs(allFiles);

//        canceled feature
//        docContentProcessor.updateInvalidFilesToEs(allFiles);
//        canceled feature
//        docContentProcessor.removeInvalidFilesFromMap(allFiles);

        //4. save new files to path and delete old dbfiles
        String dbFile = fileDataStorage.saveToDbFile(allFiles);
        log.info("save db file to {}", dbFile);

        try {
            Files.list(Paths.get(""))
                    .filter(e -> e.getFileName().toString().matches("fileDb.*"))
                    .filter(e -> !e.toAbsolutePath().toString().equals(dbFile))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                            log.info("delete old Dbfile {}", path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        workerProfile.setStatus("ready to work");
        eventPublisher.publishEvent(new MessyDocEvent("update"));
    }
}
