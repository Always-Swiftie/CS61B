package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.List;

public class HistoryTextHandler extends NgordnetQueryHandler {

    /*
    这个类用于处理当我们在网页点击生成文本形式的单词加权图的操作，构造方法要求传入一个已经构建好的映射库
    map里面存储了数据库中每个单词的时间序列，加权时间序列等等，调用方法即可
     */
    NGramMap map;
    public  HistoryTextHandler(NGramMap map){
        this.map=map;//建立映射库
    }

    @Override
    /*
    q是一个查询集合，包含所有我们需要查询的单词
     */
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();//获取待查询单词
        int startYear = q.startYear();
        int endYear = q.endYear();//获取开始和结束年份
        StringBuilder respond = new StringBuilder();
        //最终我们得到的是每个待查询单词的加权时间序列，所有需要通过构造方法传入的ngm库来找到
        for (String word : words) {
            if (map.containWord(word)) {
                TimeSeries curTS = map.weightHistory(word, startYear, endYear);//获取一个当前单词的加权时间序列，需要转换成字符串
                respond.append(word).append(": ");
                respond.append(curTS.toString());
                respond.append('\n');//每处理完一行一个单词的数据，加一个换行

            }
        }
        return respond.toString();
    }
}
