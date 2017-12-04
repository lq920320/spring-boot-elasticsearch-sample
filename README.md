# spring-boot-elasticsearch-sample
Spring boot + spring-boot-starter-data-elasticsearch + gradle


**前言：** 
Elasticsearch是一个基于Apache Lucene(TM)的开源搜索引擎。无论在开源还是专有领域，Lucene可以被认为是迄今为止最先进、性能最好的、功能最全的搜索引擎库。但是，Lucene只是一个库。想要使用它，你必须使用Java来作为开发语言并将其直接集成到你的应用中，更糟糕的是，Lucene非常复杂，你需要深入了解检索的相关知识来理解它是如何工作的。
Elasticsearch也使用Java开发并使用Lucene作为其核心来实现所有索引和搜索的功能，但是它的目的是通过简单的RESTful API来隐藏Lucene的复杂性，从而让全文搜索变得简单。
不过，Elasticsearch不仅仅是Lucene和全文搜索，我们还能这样去描述它：       
- 分布式的实时文件存储，每个字段都被索引并可被搜索         
- 分布式的实时分析搜索引擎
- 可以扩展到上百台服务器，处理PB级结构化或非结构化数据
           
而且，所有的这些功能被集成到一个服务里面，你的应用可以通过简单的RESTful API、
各种语言的客户端甚至命令行与之交互。上手Elasticsearch非常容易。它提供了许多合理的缺省值，并对初学者隐藏了复杂的搜索引擎理论。它开箱即用（安装即可使用），只需很少的学习既可在生产环境中使用。
Elasticsearch在Apache 2 license下许可使用，可以免费下载、使用和修改。随着你对Elasticsearch的理解加深，你可以根据不同的问题领域定制Elasticsearch的高级特性，这一切都是可配置的，并且配置非常灵活。

## 安装ElasticSearch

#### 1.windows 下载安装包
安装Elasticsearch唯一的要求是安装官方新版的Java，地址：www.java.com           
你可以从 [elasticsearch.org\/download](https://www.elastic.co/downloads/elasticsearch) 下载最新版本的Elasticsearch。

#### 2.docker 下载官方镜像
docker的安装过程非常简单，只需执行``` docker pull elasticsearch ```命令即可。        
elasticsearch的docker官方镜像地址：https://hub.docker.com/_/elasticsearch/ ，然后可选择合适的版本进行下载安装。

安装成功访问 http://localhost:9200 地址可验证是否安装成功：   
```
{
  "name" : "Maggott",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "1GKnrcPIQMyMrJH7F6K4Pw",
  "version" : {
    "number" : "2.4.6",
    "build_hash" : "5376dca9f70f3abef96a77f4bb22720ace8240fd",
    "build_timestamp" : "2017-07-18T12:17:44Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}
```
#### 3.版本          
由于spring boot的版本限制，这里我们需要下载的压缩包为elasticsearch-2.4.6.        
版本对应关系如下（参考：
https://github.com/spring-projects/spring-data-elasticsearch/wiki/Spring-Data-Elasticsearch---Spring-Boot---version-matrix ），
可根据实际情况下载：          

|Spring Boot Version (x)| Spring Data Elasticsearch Version (y) | Elasticsearch Version (z)| 
|---|---|---|
| x <= 1.3.5| y <= 1.3.4 | z <= 1.7.2* |
| x >= 1.4.x| 2.0.0 <=y < 5.0.0** | 2.0.0 <= z < 5.0.0**|

***

(*) - require manual change in your project pom file (solution 2.)

(**) - Next big ES release with breaking changes

***

## spring boot 所需依赖及配置

本项目是用gradle管理的spring-boot项目，在gradle添加依赖：
```groovy
compile('org.springframework.boot:spring-boot-starter-data-elasticsearch')
compile('net.java.dev.jna:jna:4.3.0')
```
```compile('net.java.dev.jna:jna:4.3.0')```可添加也可不添加。    
项目运行的配置文件application.properties中必需配置：    
```
spring.data.elasticsearch.properties.host=127.0.0.1
spring.data.elasticsearch.properties.port=9300
```

## 相关基础名词
   
**文档：** Elasticsearch是面向文档(document oriented)的，这意味着它可以存储整个对象或文档(document)。然而它不仅仅是存储，还会索引(index)每个文档的内容使之可以被搜索。在Elasticsearch中，你可以对文档（而非成行成列的数据）进行索引、搜索、排序、过滤。
Elasticsearch使用Javascript对象符号(JavaScript Object Notation)，也就是JSON，作为文档序列化格式。  
通常，我们可以认为对象(object)和文档(document)是等价相通的。不过，他们还是有所差别：
对象(Object)是一个JSON结构体——类似于哈希、hashmap、字典或者关联数组；对象(Object)中还可能包含其他对象(Object)。 
在Elasticsearch中，文档(document)这个术语有着特殊含义。它特指最顶层结构或者根对象(root object)序列化成的JSON数据（以唯一ID标识并存储于Elasticsearch中）。
     
**索引：** 在Elasticsearch中存储数据的行为就叫做索引(indexing)。在Elasticsearch中，文档归属于一种类型(type),而这些类型存在于索引(index)中，我们可以画一些简单的对比图来类比传统关系型数据库：            
```
Relational DB -> Databases -> Tables -> Rows -> Columns
Elasticsearch -> Indices   -> Types  -> Documents -> Fields
```
Elasticsearch集群可以包含多个索引(indices)（数据库），每一个索引可以包含多个类型(types)（表），每一个类型包含多个文档(documents)（行），然后每个文档包含多个字段(Fields)（列）。
> ### 「索引」含义的区分    
>   你可能已经注意到索引(index)这个词在Elasticsearch中有着不同的含义，所以有必要在此做一下区分:      
  > - 索引（名词） 如上文所述，一个索引(index)就像是传统关系数据库中的数据库，它是相关文档存储的地方，index的复数是indices 或indexes。
  > - 索引（动词） 「索引一个文档」表示把一个文档存储到索引（名词）里，以便它可以被检索或者查询。这很像SQL中的INSERT关键字，差别是，如果文档已经存在，新的文档将覆盖旧的文档。
  > - 倒排索引 传统数据库为特定列增加一个索引，例如B-Tree索引来加速检索。Elasticsearch和Lucene使用一种叫做倒排索引(inverted index)的数据结构来达到相同目的。

默认情况下，文档中的所有字段都会被索引（拥有一个倒排索引），只有这样他们才是可被搜索的。
      

## 新增索引


## 查询结果


## 更新索引


## 删除索引