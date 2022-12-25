package com.zbro.messydoc.worker.file;

import com.zbro.messydoc.commons.document.DocTypeEnum;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class FileRawEntity implements Serializable {
    @EqualsAndHashCode.Include
    private String filePath;
    @EqualsAndHashCode.Include
    private String fileName;
    @EqualsAndHashCode.Include
    private String fileHash;
    private DocTypeEnum fileType;
    private long size;
}
