package com.zbro.messydoc.worker.reader;


import com.zbro.messydoc.commons.document.DocTypeEnum;

public interface FileContentReader {

    public DocTypeEnum getType();
    public String read(String path);

}
