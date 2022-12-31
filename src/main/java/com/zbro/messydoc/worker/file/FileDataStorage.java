package com.zbro.messydoc.worker.file;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class FileDataStorage {


    public Object loadFromDbFile(){
        try{
            Path file = Files.list(Paths.get("")).filter(e->e.getFileName().toString().matches("fileDb.*")).findAny().get();
            return loadFromDbFile(file);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public Object loadFromDbFile(Path filePath){
        return loadFromDbFile(filePath.toString());
    }

    public Object loadFromDbFile(String paths){
        Object set = null;
        long startTime = System.currentTimeMillis();
        if( Files.exists(Paths.get(paths))) {
            try{
                set = (new ObjectInputStream(new FileInputStream(paths))).readObject();
            }catch (Exception e){
                e.printStackTrace();
            }
            long spendedTime = System.currentTimeMillis() - startTime;
            log.info("load DBfile spend {} ms", spendedTime);
            return set;
        }else{
            return null;
        }

    }

    public String saveToDbFile(Object object){
        return saveToDbFile("",object);
    }

    public String saveToDbFile(Object object, String ...pathsOfDoc){
        return saveToDbFile("",object,pathsOfDoc);
    }

    /**
     *
     * @param path the path where the documents-collection scanned, used to make a dbfile identification
     * @param object the object stored
     * @param pathsOfDoc the paths of the document scanned
     * @return the path of the file stored
     */
    public String saveToDbFile(String path, Object object, String ...pathsOfDoc){
        if(Files.isDirectory(Paths.get(path))){
            path = Paths.get(path).toAbsolutePath().toString();
        }
        if(pathsOfDoc.length > 0){
            Set<String> sortedPath = new TreeSet<>(Arrays.asList(pathsOfDoc));
            String pathHash = MD5Hash.digest(Arrays.toString(sortedPath.toArray(new String[0])));
            path = Paths.get(path).resolve("fileDb" + "-" + pathHash).toString();
        }else {
            path = Paths.get(path).resolve("fileDb" + "-" + MD5Hash.digest(path)).toString();
        }
        try{
            FileOutputStream fileOutputStream
                    = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }
}
