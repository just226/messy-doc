package com.zbro.messydoc.commons.elastic;

import com.zbro.messydoc.commons.document.DocumentEntityOldVer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsOldDocumentRepo extends ElasticsearchRepository<DocumentEntityOldVer,String>{

}
