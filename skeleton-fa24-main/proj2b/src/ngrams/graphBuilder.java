package ngrams;

import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//用于从文件读取数据，建立数据库
public class graphBuilder {

    wordGraph wg;

    HashMap<Integer,Set<Integer>> hyponyms;//记录每个节点的下位词节点，不必单独设置一个边类
    public graphBuilder(String synFilename,String hypoFilename){
        /*
        从同义词文件读取，建立的是节点数据
        从下位词文件读取，建立的是下位关系
         */
        this.wg=new wordGraph();
        In synIn=new In(synFilename);
        readSyn(synIn);

        In HypoIn=new In(hypoFilename);
        readHypo(HypoIn);
    }
    /*
    一行第一个数据为节点编号，后面若干个字符串是节点内单词
     */
    private void readSyn(In synIn){
        if(synIn==null){
            throw new NullPointerException("read failed!");
        }
        while (synIn.hasNextLine()){
            String nextLine=synIn.readLine();
            String[] split=nextLine.split(",");//按照逗号分割

            Set<String> curWords=new HashSet<>();
            int ID=Integer.parseInt(split[0]);//记录当前节点id
            for(int i=1;i<split.length-1;i++){
                curWords.add(split[i]);//记录节点单词
                //对于当前行数据记录过的单词，也要记录这些单词在哪些节点编号中出现过
                wg.setAppearPlace(split[i],ID);//
            }

            wg.addVertex(ID,curWords);//新建节点

        }

    }

    /*
    每行数据第一个数作为上位，后面的都是它的直接下位词节点
     */
    private void readHypo(In hypoIn){
        if(hypoIn==null){
            throw new NullPointerException("read failed!");
        }
        while (hypoIn.hasNextLine()){
            String nextLine=hypoIn.readLine();
            String[] split=nextLine.split(",");

            int curVertex=Integer.parseInt(split[0]);
            Set<Integer> Hypos=new HashSet<>();
            for(int i=1;i<split.length;i++){
                int vertex=Integer.parseInt(split[i]);//记录直接下位
                Hypos.add(vertex);
            }

            wg.addHyponyms(curVertex,Hypos);//完成下位关系构建
        }

    }

    //到此应该完成一个wordGraph对象的构建了
    public wordGraph getWg(){
        return this.wg;
    }

}
