package com.zbro.messydoc.worker.document;

import com.zbro.messydoc.commons.document.DocumentEntity;
import com.zbro.messydoc.worker.file.FileRawEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DocumentProcessor {

    public Set<DocumentEntity> aggregateDoc(Set<FileRawEntity> set){
        Map<DocumentEntity, ArrayList<String>> fileAggregateListMap = new HashMap<>();

        Set<DocumentEntity> result = new HashSet<>();
        long version = System.currentTimeMillis();
        set.forEach(e->{
            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setFileName(e.getFileName());
            documentEntity.setFileHash(e.getFileHash());
            documentEntity.setFileType(e.getFileType());
            documentEntity.setSize(e.getSize());

            documentEntity.setVersion(version);
            if(fileAggregateListMap.containsKey(documentEntity)){
                fileAggregateListMap.get(documentEntity).add(e.getFilePath());
            }else fileAggregateListMap.put(documentEntity,new ArrayList<String>(){{add(e.getFilePath());}});
        });
        fileAggregateListMap.forEach((k,v)->{
            k.setFilePath(v);
            result.add(k);
        });
        return result;
    }
    public Map<DocumentEntity, List<String>> checkNeedUpdateContent(Map<DocumentEntity, List<String>> oldFiles,
                                                                         Map<DocumentEntity, List<String>> newFiles){

        return newFiles;
    }
}
