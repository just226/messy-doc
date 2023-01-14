package com.zbro.messydoc.worker.file;

import com.zbro.messydoc.worker.document.DocTypeDetector;
import com.zbro.messydoc.commons.document.DocTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class FileScanner {

    private Set<FileRawEntity> scan0(File[] filePaths){
        Set<FileRawEntity> finalSet = new HashSet<>();

        for (File path : filePaths) {
            try {
                Files.walkFileTree(path.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        DocTypeEnum type = DocTypeDetector.getTypeFromExtensionName(file);
                        String hash = "na";
                        if(type!=DocTypeEnum.unknown) hash = MD5Hash.digest(file);
                        finalSet.add(new FileRawEntity(file.toString(),
                                file.getFileName().toString(),
                                hash,
                                type,
                                attrs.size()));
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

        }
        return finalSet;

    }
    public  Set<FileRawEntity> scan() {
        return scan0(File.listRoots());
    }

    public  Set<FileRawEntity> scan(String ...stringPaths) {
        List<File> paths = new ArrayList<>();
        for(String path:stringPaths){
            if(new File(path).isDirectory()){
                paths.add(new File(path));
            }else log.warn("{} is not a directory",path);
        }
        if(!paths.isEmpty()){
            return scan0(paths.toArray(new File[0]));
        }else return null;
    }





}
