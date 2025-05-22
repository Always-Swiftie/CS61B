package ngrams;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import org.knowm.xchart.XYChart;
import plotting.Plotter;

import java.util.ArrayList;
import java.util.List;

public class HistoryHandler extends NgordnetQueryHandler {

    NGramMap map;
    public HistoryHandler(NGramMap map){
        this.map=map;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words=q.words();
        int startYear=q.startYear();
        int endYear=q.endYear();
        /*
        我们需要新的时间序列来绘图，获取X,Y轴信息
        并且对于单词集合中每一个单词都要执行
         */
        ArrayList<TimeSeries> lts = new ArrayList<>();//用于当前单词的时间序列
        ArrayList<String> labels = new ArrayList<>();//用于存储标签-单词名称
        for(String word:words){
            if(map.containWord(word)) {
                TimeSeries curWord = map.weightHistory(word, startYear, endYear);//获取当前单词的加权时间序列
                lts.add(curWord);
                labels.add(word);
            }
        }
        XYChart chart=Plotter.generateTimeSeriesChart(labels,lts);
        String encodedImage=Plotter.encodeChartAsString(chart);

        return encodedImage;
    }
}
