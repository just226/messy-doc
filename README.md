# messy-doc
A distributed master/worker multi-thread full-text document content search app

## Description
I always get overwhelmed by a bunch of document in my computer, no matter how hard I try to organize them, they still stay in a heap of mess.

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

## Contact