package com.d3test;


import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;

import java.util.Iterator;
import java.util.List;

public class GetPaths {

    static Driver driver = null;

    public static void getDriver(){
        String uri = "bolt://localhost:7687";
        String user = "neo4j";//写你自己的neo4j的用户名
        String password = "123456";//写你自己的neo4j的密码
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user,password));
    }

    public static void close(){
        if(driver!=null){
            driver.close();
        }
    }

    public static void getPathsInfo(String cypher){
        getDriver();

        int count = 0;
        try(Session session = driver.session()){
            //result包含了所有的path
            StatementResult result = session.run(cypher);

            while(result.hasNext()){
                Record record = result.next();
                List<Value> value = record.values();

                for(Value i:value){
                    Path path = i.asPath();
                    //处理路径中的关系
                    Iterator<Relationship> relationships = path.relationships().iterator();

                    while(relationships.hasNext()){
                        count++;
                        Relationship relationship = relationships.next();

                        long startNodeId = relationship.startNodeId();
                        long endNodeId = relationship.endNodeId();
                        String relType = relationship.type();

                        System.out.println("关系"+count+"： ");
                        System.out.println("关系类型："+relType);
                        System.out.println("from "+startNodeId+"-----"+"to "+endNodeId);

                        System.out.println("关系属性如下：");

                        //得到关系属性的健
                        Iterator<String> relKeys = relationship.keys().iterator();
                        //这里处理关系属性
                        while(relKeys.hasNext()){
                            String relKey = relKeys.next();
                            String relValue = relationship.get(relKey).asObject().toString();
                            System.out.println(relKey+"-----"+relValue);
                        }
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                }
            }
        }
        close();
    }

    public static void main(String... args){

        String cypher = "match p=shortestPath((bacon:Person {name:\"Kevin Bacon\"})-[*]-(meg:Person {name:\"Meg Ryan\"})) return p";
        getPathsInfo(cypher);
    }

}
