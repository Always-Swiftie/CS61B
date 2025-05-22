package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.vertex;
import ngrams.wordGraph;

import java.util.*;
import java.util.stream.Collectors;

/*
    这个类用于处理查找若干个词的共同下位词
 */
public class HyponymsHandler extends NgordnetQueryHandler {

    private wordGraph wordG;
    private NGramMap ngm;

    HashMap<String,Integer> wordFrequent;//记录每个单词在给定时间范围内出现的总次数

    public HyponymsHandler(wordGraph wordGraph, NGramMap ngm){
        this.wordG= wordGraph;
        this.ngm=ngm;
        this.wordFrequent=new HashMap<>();
    }

    @Override
    public String handle(NgordnetQuery q) {
        //先获取带查询单词集合
        List<String> words=q.words();

        /*
        我们最终需要的是两个单词的共同下位词，取交集，也就是比如在word1中得到一个下位词，看看word2的下位词中有没有它，有则可以加入到结果中
        而且需要遍历找到所有的下位词
         */

        //获取附加信息，时间区间和K
        int startYear=q.startYear();
        int endYear=q.endYear();
        int k=q.k();

        Set<String> hypos=null;

        for(String word:words){
            //一个单词可能出现在很多个节点，那么需要一个集合找出存在这个单词的节点，再对这些节点都执行DFS，得到所有的下位词
            if(wordG.containWord(word)) {
                //获取第一个单词所有的下位词，然后得到别的单词的下位词时，要看看获取的单词是否在集合中没有出现，要取交集
                Set<String> curHypos = wordG.getAllHypos(word);

                    if(hypos==null){
                        // 第一次赋值，用当前单词的下位词初始化 hypos
                        hypos=new HashSet<>(curHypos);
                    }else{
                        // 后续交集操作，保留共同下位词
                        hypos.retainAll(curHypos);
                    }
            }
        }
        if(hypos==null){
            return "[]";
        }
        hypos.remove("dummy");
        //现在hypos应该已经得到了所有待查询单词的共同下位词了

        StringBuilder result=new StringBuilder();

        List<String> sorted=new ArrayList<>(hypos);
        Collections.sort(sorted);
        //得到已排序好的共同下位词
        if(k==0) {//如果k=0，无需考虑
            for (int i = 0; i < sorted.size(); i++) {
                result.append(sorted.get(i));
                if (i != sorted.size() - 1) {
                    result.append(", ");
                }
            }
        }else{//如果k!=0,才需要处理
            /*
            遍历sorted集合中每一个出现过的单词，先看看在ngram里面有没有
             */
            for(String word:sorted){
                if(ngm.containWord(word)){//如果当前单词在ngrammap中存在，再去根据它的时间序列，求出总的出现频率
                    int curCount=ngm.getTotalCount(word,startYear,endYear);
                    wordFrequent.put(word,curCount);//记录当前单词出现的次数
                }
            }//得到了每一个在ngm中出现过的单词的出现频率
            PriorityQueue<Map.Entry<String,Integer>> priorityQueue=new PriorityQueue<>(
                    (a,b)->{
                        int freqCompare=b.getValue().compareTo(a.getValue());//比较两个单词的出现频率，作为优先队列的构建规则
                        if(freqCompare!=0){
                            return freqCompare;
                        }
                        return a.getKey().compareTo(b.getKey());//如果出现频率一样，按字典序排序
                    }
            );
            priorityQueue.addAll(wordFrequent.entrySet());
            //完成优先队列的构建，出队k次即可

                List<String> hypoWords=new ArrayList<>();
                for(int i=0;i<k;i++){
                    if(priorityQueue.isEmpty()){
                        break;
                    }
                    hypoWords.add(priorityQueue.poll().getKey());

                }
                //对hypowords也排序

                Collections.sort(hypoWords);
                for (int i = 0; i <hypoWords.size(); i++) {
                result.append(hypoWords.get(i));
                if (i != hypoWords.size() - 1) {
                    result.append(", ");
                }
            }
        }

        return result.toString();
    }
}
