package com.zbro.messydoc.master.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import com.zbro.messydoc.commons.document.NewDocumentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class MessyDocController {

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    ElasticsearchClient elasticsearchClient;

    @GetMapping("index")
    public String getIndex(Model model) {
        model.addAttribute("highLightKey", "place holder 占位符");
        model.addAttribute("NameList",new ArrayList<>());
        model.addAttribute("ContentList",new ArrayList<>());
        return "index";
    }

    @GetMapping("find")
    public String getText(@RequestParam String key, Model model) throws Exception{
        if(key.equals("")){
            model.addAttribute("highLightKey", "place holder 占位符");
            model.addAttribute("NameList",new ArrayList<>());
            model.addAttribute("ContentList",new ArrayList<>());
            return "index";
        }
        List<String> highLightKeys = new LinkedList<>();

        elasticsearchClient
                .indices()
                .analyze(AnalyzeRequest.of(e->{
                    e.analyzer("ik_max_word");
                    e.text(key);
                    return e;
                }))
                .tokens().forEach(e->highLightKeys.add(e.token()));

        NativeQuery queryFileContent = NativeQuery.builder()
                .withQuery(q->q.match(m->m.field("fileContent").query(key))).withMaxResults(20)
                .build();
        NativeQuery queryFileName = NativeQuery.builder()
                .withQuery(q->q.matchPhrase(v->v.field("fileName").query(key))).withMaxResults(20)
                .build();
        long t = System.currentTimeMillis();

        //todo switch the entity
        List<NewDocumentEntity> fileContentList = new LinkedList<>();
        List<NewDocumentEntity> fileNameList = new LinkedList<>();
        operations.search(queryFileContent,NewDocumentEntity.class).forEach(e->fileContentList.add(e.getContent()));
        operations.search(queryFileName,NewDocumentEntity.class).forEach(e->fileNameList.add(e.getContent()));
        log.info("query spend {} ms", System.currentTimeMillis() - t);

//        Pattern p = Pattern.compile(".{20,100}");

        model.addAttribute("ContentList",fileContentList
                .stream()
                .peek(e -> {
                    String content = e.getFileContent();
                    e.setFileContent("");
                    for (String hLK:highLightKeys){
                        Pattern p = Pattern.compile(".{0,30}" + hLK + ".{0,30}");
                        Matcher m = p.matcher(content);
                        if(m.find()){
                            e.setFileContent(e.getFileContent() + "......" + m.group());
                        }
                    }
                })
                .collect(Collectors.toList()));

        model.addAttribute("NameList",fileNameList);
        model.addAttribute("highLightKey", highLightKeys.get(0));
        return "index";
    }

    @GetMapping("content/{id}")
    public String getContent(@RequestParam Optional<String> key, @PathVariable("id") String id, Model model) {
        //todo switch the entity

        NewDocumentEntity d = operations.get(id,NewDocumentEntity.class);
        model.addAttribute("item", d);
        model.addAttribute("ver", d.getVersion() != 0 ? new Date(d.getVersion()) : "the file has been deleted");
        model.addAttribute("highLightKey",key.isPresent() ? key.get(): "place holder 占位符");
        return "contentPage";
    }

}
