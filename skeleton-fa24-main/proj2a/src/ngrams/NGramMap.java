package ngrams;

import edu.princeton.cs.algs4.In;

import java.sql.Time;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static ngrams.TimeSeries.MAX_YEAR;
import static ngrams.TimeSeries.MIN_YEAR;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {

    /*
    构造器传入的参数分别是单词统计文件和年份单词总数
    countHistory方法传入目标单词，要求返回该单词的时间序列，那么我们就需要事先拥有这个单词的各种数据，比如他在哪些年出现？
    出现次数等等
    需不需要对单词文件中每个出现过的单词执行存储操作？将单词作为key,每个key(单词)拥有唯一的时间序列，用户传入开始和结束年份，我们就
    返回一个时间序列的副本即可
     */

    HashMap<String,TimeSeries> wordWithTS;//用一个HashMap存储一个单词的时间序列-TS类存储了年份和数据的映射
    HashMap<Integer,Long> YearAndCount;//用一个HashMap存储每一年的单词总数

    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        /*
        我们需要从wordsfile文件中读取每一行，包含单词名，年份，该单词在该年份出现次数
        countsfile每一行记录了年份，这一年的总单词数
         */
        wordWithTS=new HashMap<>();
        YearAndCount=new HashMap<>();
        //先读取记录每年的单词总数
        In countin=new In(countsFilename);
        readCount(countin);
        //再处理每一行单词数据
        In wordsin=new In(wordsFilename);
        readWords(wordsin);
        //到此应该处理完了
    }

    private void readCount(In countin){
        while (countin.hasNextLine()){
            String nextLine=countin.readLine();
            String[] split=nextLine.split(",");//按照逗号分割,第一个元素为年份，第二个为单词总数
            Integer year=Integer.parseInt(split[0]);
            Long count=Long.parseLong(split[1]);
            YearAndCount.put(year,count);
        }
    }

    private void readWords(In wordsin){
        while (wordsin.hasNextLine()){
            String nextLine=wordsin.readLine();//读取一行数据
            String[]split=nextLine.split("\t");
            //按照文件格式，split数组中第一个元素为当前行的单词，第二个元素为年份，第三个元素为出现次数
            //读到单词，如果当前单词还没有被读取过，那么需要创建新的映射
            String cur_word=split[0];
            int year=Integer.parseInt(split[1]);
            //获取当前年份的单词总数
            Double value=Double.parseDouble(split[2]);
            if(!wordWithTS.containsKey(cur_word)) {
                //当前单词还没创建映射
                TimeSeries newTS=createNewTS(year,value);
                wordWithTS.put(cur_word,newTS);
            }else{//如果当前单词已经建立映射，更新它的时间序列即可
                TimeSeries curTS=wordWithTS.get(cur_word);
                curTS.put(year,value);
            }
        }
    }


    private TimeSeries createNewTS(int year,Double value){
        TimeSeries newTS=new TimeSeries();
        newTS.put(year,value);
        return newTS;
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        // TODO: Fill in this method.
        if(!wordWithTS.containsKey(word)){
            return null;
        }
        /*
        返回给定单词从startYear到endYear之间的时间序列，调用在TS类已经写好的方法
         */
        TimeSeries source=wordWithTS.get(word);//先获取单词的时间序列本身
        //再根据时间创建副本

        return new TimeSeries(source,startYear,endYear);
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {

        if(!wordWithTS.containsKey(word)){
            return null;
        }
        //返回时间序列的副本
        return wordWithTS.get(word);
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        //根据年份-数据的映射创建时间序列
        TimeSeries totalCountHistory=new TimeSeries();
        //在上面已经把数据记录在哈希表中了，拆除了即可
        for(int year:YearAndCount.keySet()){
            Long count=YearAndCount.get(year);
            totalCountHistory.put(year, Double.valueOf(count));
        }
        return totalCountHistory;
    }

    private TimeSeries Count_History(int startYear,int endYear){
        TimeSeries source=totalCountHistory();
        return new TimeSeries(source,startYear,endYear);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        if(!wordWithTS.containsKey(word)){
            return null;
        }
        //这里需要创建一个新的wordWithTS映射，获取每个单词在某一年的出现次数，再获取当年的总单词数，二者相除得到加权频率
        TimeSeries source=countHistory(word,startYear,endYear);
        TimeSeries counts=Count_History(startYear,endYear);//按照短点年份建立一个年份-单词数的时间序列
        for(int year:counts.years()){
            //在开始和结束年份之间不一定每一年当前单词都有映射
            if(source.containsKey(year)) {
                Double totalCount = counts.get(year);
                Double count = source.get(year);
                Double newValue = count / totalCount;
                source.put(year, newValue);
            }
        }
        return source;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        if(!wordWithTS.containsKey(word)){
            return null;
        }
        TimeSeries source=countHistory(word);//获取原单词的时间序列
        TimeSeries counts=totalCountHistory();//获取总的计数时间序列
        for(int year: counts.years()){
            Double totalCount=counts.get(year);
            Double count=source.get(year);
            Double newValue=count/totalCount;
            source.put(year,newValue);
        }
        return source;
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {
        /*
        对集合中的每一个单词都构造一个加权的时间序列，对他们调用plus方法，最后得到的时间序列value也是相对频率
         */
        TimeSeries summed=createEmptyTS(startYear,endYear);
        for(String word:words){
            if(!wordWithTS.containsKey(word)){
                continue;
            }
            TimeSeries curWordTS=weightHistory(word,startYear,endYear);
            summed=summed.plus(curWordTS);
        }
    return summed;
    }

    private TimeSeries createEmptyTS(int startYear,int endYear){
        TimeSeries newTS=new TimeSeries();
        Set<Integer> years=YearAndCount.keySet();
        for(int year:years){
            if(year>=startYear&&year<=endYear){
                newTS.put(year,0.0);
            }
        }
        return newTS;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        return null;
    }

    public boolean containWord(String word){
        return wordWithTS.containsKey(word);
    }

}
