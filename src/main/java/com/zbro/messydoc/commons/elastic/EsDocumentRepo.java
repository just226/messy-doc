package com.zbro.messydoc.commons.elastic;

import com.zbro.messydoc.commons.document.DocumentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsDocumentRepo extends ElasticsearchRepository<DocumentEntity,String>{

}
