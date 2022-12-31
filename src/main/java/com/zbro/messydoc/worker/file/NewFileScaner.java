package com.zbro.messydoc.worker.file;

import com.zbro.messydoc.commons.document.DocTypeEnum;
import com.zbro.messydoc.commons.document.DocumentEntity;
import com.zbro.messydoc.commons.document.NewDocumentEntity;
import com.zbro.messydoc.worker.document.DocTypeDetector;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Slf4j
public class NewFileScaner {

    private boolean scan0(Map<String, NewDocumentEntity> documents, File[] filePaths){

        //invalid the version;
        documents.values().forEach(e->e.setVersion(0));

        for (File path : filePaths) {
            long startTime = System.currentTimeMillis();
            try {
                Files.walkFileTree(path.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
                        DocTypeEnum type = DocTypeDetector.getTypeFromExtensionName(file);
                        String hash = type!=DocTypeEnum.unknown ? MD5Hash.digest(file) : "";
                        String fileName = file.getFileName().toString();
                        String key = fileName+hash;

                        //1.the version, filescan will touch every files, un-updated version indicates the file is not exist;
                        //2.the key, files with different name but same content have different key, they all will be persist;
                        //3.the key, unknown type files are considered as common assets, those have same name will be aggregated to 1 entry;

                        //double brace map initiation makes trouble, the element apply the type of the outer-class
                        documents.compute(key,(k,v)->{
                            if(v == null){
                                NewDocumentEntity documentEntity = new NewDocumentEntity();
                                documentEntity.setFileName(fileName);
                                documentEntity.setFileType(type);
                                documentEntity.setSize(attrs.size());
                                documentEntity.setVersion(startTime);
                                documentEntity.setNeedUpdateContent(true);
                                HashSet<String> paths = new HashSet<>();
                                paths.add(file.toString());
                                documentEntity.setFilePath(paths);
                                return documentEntity;
                            }else {
                                v.getFilePath().add(file.toString());
                                v.setVersion(startTime);
                                return v;
                            }
                        });
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            long spendedTime = System.currentTimeMillis() - startTime;
            log.info("file scan spend {} ms", spendedTime);
            log.info("size is: {}",documents.size());
        }
        return true;

    }
    public  boolean scan() {
        return scan0(new HashMap<>(), File.listRoots());
    }

    public  boolean scan(Map<String, NewDocumentEntity> oldDocuments, String ...stringPaths) {
        List<File> paths = new ArrayList<>();
        for(String path:stringPaths){
            if(new File(path).isDirectory()){
                paths.add(new File(path));
            }else log.info("{} is not a directory",path);
        }
        if(!paths.isEmpty()){
            return scan0(oldDocuments, paths.toArray(new File[0]));
        }else return false;
    }

    public  boolean scan(String ...stringPaths) {
        List<File> paths = new ArrayList<>();
        for(String path:stringPaths){
            if(new File(path).isDirectory()){
                paths.add(new File(path));
            }else log.info("{} is not a directory",path);
        }
        if(!paths.isEmpty()){
            return scan0(new HashMap<>(), paths.toArray(new File[0]));
        }else return false;
    }





}
