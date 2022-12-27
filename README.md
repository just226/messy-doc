# messy-doc
A distributed master/worker multi-thread full-text document content search app

## Description
I always get overwhelmed by the massives bunch of documents in my computer, no matter how hard I try to organize them, they still stay in a heap of mess.

Messy-doc helps me to find the right things I want.

## Usage
1. You need to set up an elasticsearch

If you have docker compose, you can put this in your configuration yml file.

```version: "3"
services:
  es:
    image: elasticsearch:8.5.3
    container_name: es
    hostname: es
    ports:
      - '9200:9200'
    environment: 
      - discovery.type=single-node
      - xpack.security.enabled=false
      - xpack.security.http.ssl.enabled=false
```

Test the es:

```
curl http://youres:9200
```

Yes! There is no authentication or ssl, it will save us a lot of time. But it is not a good idea to run naked in open field or your production enviroment, take good care of your privacy.

Then, install the ik-analyzer. 

```
unzip elasticsearch-analysis-ik-7.17.3.zip  -d ik
docker cp ./ik es:/usr/share/elasticsearch/plugins/ik
docker restart es
```
Test your analyzer
```
GET _analyze
{
  "text": "蠢货！我们的事业是正义的！"
  , "analyzer": "ik_max_word"
}
```
If your document are in English like language, you may not need ik, and Modify the Entity in 

```
com.zbro.messydoc.commons.document.DocumentEntity.java
```

2. this is a spring-boot app, check the properties in the application.properties

The only thing you need to modify is es host, leave the others untouched unless you know what are they used for.
```
elastic.host=192.168.240.134
```
3. build and run, explore and enjoy

Messy-doc is at: http://yourhost:13399/index

## Todo

1. Here's the begining of this App, a lot of things need to do like 

2. The jar is really fat and need to lose some weight, maybe decoupling the master and worker will help, but the dependent jars have the most blame and I don't know how to deal with them.

3. Files in archive like zip or rar need to be process

4. There should have some security considerations, like the conversation between master and worker, the security of ES etc..

5. A comprehensive API need to be adapted, and socket have less overhead then web API

6. To process the files more efficiently by leveraging the map

7. Frontend

## Misc

I suppose Messy-doc could be use in small team, provide valuable words at hand. It is an master/worker app, you could distribute different kinds of work throughout your devices.

Feel free to show your new ideas about it or anything else.