package ngrams;

import edu.princeton.cs.algs4.In;

import java.util.List;
import java.util.Set;

public class vertex {

    int id;
    Set<String> synWords;

    //构建一个节点
    public vertex(int id){
        this.id=id;
    }
    public vertex(int id,Set<String> words){
        this.id=id;
        this.synWords=words;
    }

}
