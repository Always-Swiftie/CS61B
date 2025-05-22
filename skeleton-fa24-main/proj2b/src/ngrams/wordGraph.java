package ngrams;

import edu.princeton.cs.algs4.In;

import java.util.*;

public class wordGraph {

    HashMap<Integer,vertex> map;//使用编号为key值，一个编号对应一个节点
    HashMap<String, Set<Integer>> appearPlace;//记录一个单词在哪些编号的节点出现过

    HashMap<Integer,Set<Integer>> hyponyms;//记录每个节点的下位词节点，不必单独设置一个边类
    //构造方法，传入已经构建好的节点编号映射，词-节点映射
    public wordGraph(){
        this.map=new HashMap<>();
        this.appearPlace=new HashMap<>();
        this.hyponyms=new HashMap<>();
    }



    //新增同义词节点
    public void addVertex(int id,Set<String> words){
        this.map.putIfAbsent(id,new vertex(id,words));
    }
    //建立边 注意每一个集合开头元素是后面所有元素的直接上位词
    public void addHyponyms(int curVertex, Set<Integer> hypos){
        if (hypos == null || hypos.isEmpty() || !map.containsKey(curVertex)) return;

        //应该合并而不是覆盖
        hyponyms.computeIfAbsent(curVertex, k -> new HashSet<>()).addAll(hypos);
    }

    //更新当前单词的出现位置
    public void setAppearPlace(String word,int ID){

        appearPlace.computeIfAbsent(word,k->new HashSet<>()).add(ID);
    }

    //根据节点编号获取节点
    private vertex getVertex(int ID){
        if(!map.containsKey(ID)){
            return null;
        }
        return map.get(ID);
    }

    private Set<vertex> getVertices(String word){
        if(!appearPlace.containsKey(word)){
            return null;
        }

        Set<vertex> vertices=new HashSet<>();
        for(int ID:appearPlace.get(word)){
            //先得到当前单词出现所有节点的编号
            vertices.add(getVertex(ID));
        }

        return vertices;
    }

    
    public Set<String> getAllHypos(String word){//利用DFS深度优先搜索找出所有下位词节点，并得到里面的单词



        Set<String> allHypos= new HashSet<>();

        Set<vertex> vertices=getVertices(word);//得到当前单词出现过的所有节点-原始形式，非编号
        if(vertices==null){
            throw new IllegalArgumentException("word does not exist!");
        }

        for(vertex v:vertices){//对于当前单词出现过的每一个节点都要进行DFS
            Set<vertex> visited=new HashSet<>();//存储当前已经访问过的节点
            Stack<vertex> stack=new Stack<>();
            stack.push(v);

            while (!stack.isEmpty()){
                vertex cur=stack.pop();
                    if(!visited.contains(cur)){
                            //not visited yet,get it,set true
                        Set<String> cur_words=cur.synWords;
                        allHypos.addAll(cur_words);
                        visited.add(cur);
                            //如果当前节点还有下位节点
                            if(hyponyms.containsKey(cur.id)){
                                //让它的下位都入栈
                                for(int ID:hyponyms.get(cur.id)){
                                    vertex hyV=getVertex(ID);

                                    if(hyV!=null&&!visited.contains(hyV)){
                                        stack.push(hyV);
                                    }
                                }
                            }
                        }
                }
        }
        return allHypos;//得到了当前单词的所有下位词

    }

    public boolean containWord(String word){
        return appearPlace.containsKey(word);
    }




}
