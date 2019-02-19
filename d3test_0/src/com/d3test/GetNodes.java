package com.d3test;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetNodes {

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

    //node
    public static void getNodesInfo(String cypher){
        getDriver();

        Map<String,String> node_AttrKey_AttrValue_Map = new HashMap<>();
        //driver.isEncrypted();

        try(Session session = driver.session()){
            StatementResult result = session.run(cypher);
            System.out.println("driver.session");
            while(result.hasNext()){
                System.out.println("result.hasNext");
                Record record = result.next();
                List<Value> value = record.values();

                for(Value i:value){
                    System.out.println(value);
                    Node node = i.asNode();
                    Iterator keys = node.keys().iterator();

                    Iterator nodeTypes = node.labels().iterator();
                    String nodeType = nodeTypes.next().toString();
                    System.out.println("节点类型："+nodeType);
                    System.out.println("节点属性如下：");
                    while(keys.hasNext()){
                        String attrKey = (String)keys.next();
                        Value temp = node.get(attrKey);
                        System.out.println(attrKey + "---" + temp);
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                }
            }
        }
        close();
    }

    public static void main(String... args){

        String cypher = "MATCH (tom {name: \"Tom Hanks\"}) RETURN tom";
        //String cypher = "match (n {born:1930}) return n";
        getNodesInfo(cypher);
    }

}