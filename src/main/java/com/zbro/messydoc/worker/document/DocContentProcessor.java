package com.zbro.messydoc.worker.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest;
import com.zbro.messydoc.commons.document.NewDocumentEntity;
import com.zbro.messydoc.worker.WorkerEntitySingleton;
import com.zbro.messydoc.commons.document.DocTypeEnum;
import com.zbro.messydoc.commons.document.DocumentEntity;
import com.zbro.messydoc.worker.reader.FileContentReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class DocContentProcessor {

    @Autowired
    WorkerEntitySingleton workerProfile;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ElasticsearchClient elasticsearchClient;
    /**
     * Doesn't read the file with size exceeding 6M ,consider it not a valuable document.
     */
    private final long maxSize = 6291456;

    private Set<DocTypeEnum> fileTypeSet = new HashSet<>();
    private Map<DocTypeEnum, FileContentReader> parsers = new HashMap<>();

    public void fileParserRegister(FileContentReader parser){
        this.parsers.put(parser.getType(),parser);
        this.fileTypeSet.add(parser.getType());
    }

    public Set<DocumentEntity> initFileContent(Set<DocumentEntity> files){
        files.stream()
                .filter(e -> e.getFileType()!=DocTypeEnum.unknown)
                .forEach(e->e.setFileContent(parsers.get(e.getFileType()).read((String)e.getFilePath().get(0))));
        return files;
    }

//    public void readFileContentAndSaveToMongo(Set<DocumentEntity> files){
//         files
//                .forEach(e->{
//                    DocumentEntity documentEntity = new DocumentEntity();
//                    // parse the file which type parser has been registered by fileParserRegister
//                    if(parsers.get(e.getFileType()) != null){
//                        log.debug("retrieve {} content", e.getFileName());
//                        if(e.getFileType() == DocTypeEnum.EXCEL){
//                            // excel file process
//                            if (e.getSize() < maxSize){
//                                documentEntity.setFileContent(parsers.get(e.getFileType()).read(e.getFilePath().get(0)));
//                            }else  documentEntity.setFileContent("Ignore large excel file");
//                        }else{
//                            // un-excel file process
//                            documentEntity.setFileContent(parsers.get(e.getFileType()).read(e.getFilePath().get(0)));
//                        }
//                    }
//                    documentEntity.setFileHash(e.getFileHash());
//                    documentEntity.setHostName(dogProfile.getName());
//                    documentEntity.setFileName(e.getFileName());
//                    documentEntity.setFilePath(e.getFilePath());
//                    documentEntity.setVersion(e.getVersion());
//                    documentEntity.setNeedUpdateContent(false);
//                    mongoTemplate.save(documentEntity);
//                });
//    }

        public void readFileContentAndSaveToEs(Set<DocumentEntity> files){
         files
                .forEach(e->{
                    DocumentEntity documentEntity = new DocumentEntity();
                    // parse the file which type parser has been registered by fileParserRegister
                    if(parsers.get(e.getFileType()) != null){
                        log.debug("retrieve {} content", e.getFileName());
                        if(e.getFileType() == DocTypeEnum.EXCEL){
                            // excel file process
                            if (e.getSize() < maxSize){
                                documentEntity.setFileContent(parsers.get(e.getFileType()).read(e.getFilePath().get(0))
                                        .replaceAll("[\r|\n|\r\n]{2,}","\n")
                                        .replaceAll("[ |\t]{2,}"," ")
                                        .replaceAll("[ \r| \n| \r\n]{2,}", " \n")
                                        .replaceAll("[\t\r|\t\n|\t\r\n]{2,}", " \n")
                                );
                            }else  documentEntity.setFileContent("Ignore large excel file");
                        }else{
                            // un-excel file process
                            documentEntity.setFileContent(
                                    parsers
                                            .get(e.getFileType())
                                            .read(e.getFilePath().get(0))
                                            .replaceAll("[\r|\n|\r\n]{2,}","\n")
                                            .replaceAll("[ |\t]{2,}"," ")
                                            .replaceAll("[ \r| \n| \r\n]{2,}", " \n")
                                            .replaceAll("[\t\r|\t\n|\t\r\n]{2,}", " \n")

                            );
                        }
                    }
                    documentEntity.setFileHash(e.getFileHash());
                    documentEntity.setFileName(e.getFileName());
                    documentEntity.setFilePath(e.getFilePath());
                    documentEntity.setVersion(e.getVersion());
                    documentEntity.setNeedUpdateContent(false);
                    documentEntity.setHostName(e.getHostName());
                    elasticsearchTemplate.save(documentEntity);
                });
    }

    public void readFileContentAndSaveToEs(Map<String, NewDocumentEntity> files){
        log.info("{} files need to be sniffed",files.values()
                .stream()
                .filter(NewDocumentEntity::isNeedUpdateContent)
                .count());
        files.values().forEach(e->{
                    if(e.isNeedUpdateContent()){
                        e.setNeedUpdateContent(false);
                        NewDocumentEntity documentEntity = new NewDocumentEntity();
                        // parse the file which type parser has been registered by fileParserRegister
                        if(parsers.get(e.getFileType()) != null){
                            log.debug("retrieve {} content", e.getFileName());
                            if(e.getFileType() == DocTypeEnum.EXCEL){
                                // excel file process
                                if (e.getSize() < maxSize){
                                    String c = "";
                                    for(String p:e.getFilePath()){
                                        if(!(c = parsers.get(e.getFileType()).read(p)).equals("null")) break;
                                    }
                                    documentEntity.setFileContent(c
                                            .replaceAll("[\r|\n|\r\n]{2,}","\n")
                                            .replaceAll("[ |\t]{2,}"," ")
                                            .replaceAll("[ \r| \n| \r\n]{2,}", " \n")
                                            .replaceAll("[\t\r|\t\n|\t\r\n]{2,}", " \n")
                                    );
                                }else  documentEntity.setFileContent("Ignore large excel file");
                            }else{
                                // un-excel file process
                                String c = "";
                                for(String p:e.getFilePath()){
                                    if(!(c = parsers.get(e.getFileType()).read(p)).equals("null")) break;
                                }
                                documentEntity.setFileContent(c
                                        .replaceAll("[\r|\n|\r\n]{2,}","\n")
                                        .replaceAll("[ |\t]{2,}"," ")
                                        .replaceAll("[ \r| \n| \r\n]{2,}", " \n")
                                        .replaceAll("[\t\r|\t\n|\t\r\n]{2,}", " \n")
                                );
                            }
                            documentEntity.setFileName(e.getFileName());
                            documentEntity.setFilePath(e.getFilePath());
                            documentEntity.setVersion(e.getVersion());
                            documentEntity.setHostName(e.getHostName());
                            log.debug("(1)ready to save {}",e.getFileName());
                            elasticsearchTemplate.save(documentEntity);
                        }else{
                            //todo do the update, can't use save
                            log.debug("(2)ready to save {}",e.getFileName());
//                            Document document = Document.from(new HashMap<String,Set<String>>(){{put("filePath",e.getFilePath());}});
//                            UpdateQuery updateQuery = UpdateQuery.builder(e.getId()).build();
//                            elasticsearchTemplate.update(updateQuery,IndexCoordinates.of("new-messy-doc"));
                        }

                    }else{
                        log.debug("(3)ready to save {}",e.getFileName());
                    }
                });
    }

    public Set<DocumentEntity> updateFileContent(Set<DocumentEntity> files){
        files.stream()
                .filter(DocumentEntity::isNeedUpdateContent)
                .forEach(e->e.setFileContent(parsers.get(e.getFileType()).read((String)e.getFilePath().get(0))));
        return files;
    }

}
