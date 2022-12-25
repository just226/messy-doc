package com.zbro.messydoc.commons.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
//@Document("DocFile")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "old_document2")
public class DocumentEntityOldVer implements Serializable {
    @Id
    private String id;
    @EqualsAndHashCode.Include
    private String fileName;
    @EqualsAndHashCode.Include
    @Transient
    private String fileHash;
    private ArrayList filePath;
    @Transient
    private DocTypeEnum fileType;
    @Field(type = FieldType.Text ,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String fileContent;
    @Transient
    private boolean needUpdateContent;
    private long version;
    @Transient
    private long size;
}
