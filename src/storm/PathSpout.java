package storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import java.util.Map;
import java.util.Random;

public class PathSpout extends BaseRichSpout {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SpoutOutputCollector _collector;
    Random _rand;    
    
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
        _rand = new Random();
    }

    @Override
    public void nextTuple() {
        Utils.sleep(100);
        String[] sentences = new String[] {
            "[1,2,3,4,5]",
            "[1,3,2,4,5]",
            "[1,5,3,4,2]",
            "[1,2,5,4,3]",
            "[1,4,3,2,5]",
            "[2,1,4,3,5]",
            "[2,1,5,3,4]",
            "[3,1,4,2,5]",
            "[3,1,5,2,4]",
            "[4,1,3,2,5]",
            "[4,1,5,2,4]",
            "[5,4,3,2,1]",
            "[5,4,1,2,3]"
        };
        String sentence = sentences[_rand.nextInt(sentences.length)];
        _collector.emit(new Values(sentence));
    }        

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("PATH"));
    }
    
    
}