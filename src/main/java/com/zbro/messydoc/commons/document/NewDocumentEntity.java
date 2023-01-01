package com.zbro.messydoc.commons.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "new-messy-doc")
public class NewDocumentEntity implements Serializable {
    @Id
    private String id;
    private String fileName;
    private Set<String> filePath;
    @Field(type = FieldType.Text ,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String fileContent;
    private long version;
    private String hostName;
    @Transient
    private String fileHash;
    @Transient
    private DocTypeEnum fileType;
    @Transient
    private boolean needUpdateContent;
    @Transient
    private boolean needUpdatePath;
    @Transient
    private boolean needSetDelete;
    @Transient
    private long size;
}
