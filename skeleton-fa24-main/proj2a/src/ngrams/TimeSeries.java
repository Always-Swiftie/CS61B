package ngrams;

import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * An object for mapping a year number (e.g. 1996) to numerical data. Provides
 * utility methods useful for data analysis.
 *
 * @author Josh Hug
 */
public class TimeSeries extends TreeMap<Integer, Double> {

    /** If it helps speed up your code, you can assume year arguments to your NGramMap
     * are between 1400 and 2100. We've stored these values as the constants
     * MIN_YEAR and MAX_YEAR here. */
    public static final int MIN_YEAR = 1400;
    public static final int MAX_YEAR = 2100;

    /**
     * Constructs a new empty TimeSeries.
     */
    public TimeSeries() {
        super();
    }

    /**
     * Creates a copy of TS, but only between STARTYEAR and ENDYEAR,
     * inclusive of both end points.
     */
    public TimeSeries(TimeSeries ts, int startYear, int endYear) {
        super();
        // TODO: Fill in this constructor.
        /*TreeMap基于红黑树实现，按照key的比较排序，所以当我们遍历key值时，得到的年份是有序的
          每得到一个key,获取其value,加入到副本当中即可
         */
        for(int year:ts.keySet()) {
            if (year >= startYear && year <= endYear) {
                super.put(year, ts.get(year));
            }
        }
    }

    /**
     *  Returns all years for this time series in ascending order.
     */
    public List<Integer> years() {
        // TODO: Fill in this method.
        List<Integer> allYears = new ArrayList<>(super.keySet());
        allYears.sort(Integer::compareTo);//调用比较器，对集合里的年份进行排序
        return allYears;
    }

    /**
     *  Returns all data for this time series. Must correspond to the
     *  order of years().
     */
    public List<Double> data() {
        // TODO: Fill in this method.
        /*
        在上一个方法中我们按照升序得到了时间序列中出现的年份，只需要将年份作为key获取value即可
         */
        List<Double> allData=new ArrayList<>();
        List<Integer> allYears=years();
        for(int year:allYears){
            allData.add(super.get(year));
        }
        return allData;
    }

    /**
     * Returns the year-wise sum of this TimeSeries with the given TS. In other words, for
     * each year, sum the data from this TimeSeries with the data from TS. Should return a
     * new TimeSeries (does not modify this TimeSeries).
     *
     * If both TimeSeries don't contain any years, return an empty TimeSeries.
     * If one TimeSeries contains a year that the other one doesn't, the returned TimeSeries
     * should store the value from the TimeSeries that contains that year.
     */
    public TimeSeries plus(TimeSeries ts) {
        // TODO: Fill in this method.
        /*
        将给定时间序列ts中每年对应的value和当前时间序列中每年对应的value进行相加，得到新的时间序列
        如果ts中有的年份当前时间序列没有，也要存下，只是不用相加了
         */
        TimeSeries newTS=new TimeSeries();
        //遍历ts中出现的年份，每次获取到一个年份就看看在当前时间序列中是否也存在相同年份
        for(int year:this.years()){
            if(ts.containsKey(year)){
                Double newValue=this.get(year)+ts.get(year);
                newTS.put(year,newValue);
            }else{
                newTS.put(year,this.get(year));
            }
        }
        //这样一轮下来，当前序列出现的所有年份都能保证被添加，当前有的如果ts也有，就会被加入，但是没有考虑ts有点当前没有的情况
        for(int year:ts.years()){
            if(!this.containsKey(year)){
                newTS.put(year,ts.get(year));
            }
        }

        return newTS;
    }

    /**
     * Returns the quotient of the value for each year this TimeSeries divided by the
     * value for the same year in TS. Should return a new TimeSeries (does not modify this
     * TimeSeries).
     *
     * If TS is missing a year that exists in this TimeSeries, throw an
     * IllegalArgumentException.
     * If TS has a year that is not in this TimeSeries, ignore it.
     */
    public TimeSeries dividedBy(TimeSeries ts) {
        // TODO: Fill in this method.
        /*
        获取当前时间序列中所有年份，如果获取到的年份在给定ts中不存在，抛出错误
        否则以当前年份为key,对应的value是当前时间序列该年份的值除以ts该年份值的商
         */
        TimeSeries newTS=new TimeSeries();
        for(int year:this.years()){
            if(!ts.containsKey(year)){
                throw new IllegalArgumentException("Year dose not exist");
            }
            Double newValue=this.get(year)/ts.get(year);
            newTS.put(year,newValue);
        }
        return newTS;
    }

    // TODO: Add any private helper methods.
    // TODO: Remove all TODO comments before submitting.
}
