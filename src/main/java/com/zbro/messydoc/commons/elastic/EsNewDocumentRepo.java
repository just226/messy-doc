package com.zbro.messydoc.commons.elastic;

import com.zbro.messydoc.commons.document.NewDocumentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsNewDocumentRepo extends ElasticsearchRepository<NewDocumentEntity,String>{

}
