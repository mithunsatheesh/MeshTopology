package storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* Sample Bolt 4
*/
public class Bolt4 extends BaseRichBolt {
    
	private static final long serialVersionUID = 7949540834062031472L;
	public static final Logger LOG = LoggerFactory.getLogger(BoltDecider.class);
	private OutputCollector objCollector;
		
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		
		objCollector= collector;
		
	}	

    @Override
    public void execute(Tuple tuple) { 	
    	    	
    	JSONArray data;
		
		try {
			
			data = new JSONArray(tuple.getString(0));
		
		} catch (JSONException e) {
			
			return;
			
		}		
		
		String nextBolt = data.optString(data.length()-1);
		data.remove(data.length()-1);
		
		System.err.println(data.toString());
		
		if(data.length()-1 == 0) {
			
			this.objCollector.emit("StreamDbWriter",new Values("output"));	
			
		} else {
		
			this.objCollector.emit("StreamBolt"+nextBolt,new Values(data.toString()));	
		
		}
		
    }

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {		

		declarer.declareStream("StreamBolt1",new Fields("PATH"));
		declarer.declareStream("StreamBolt2",new Fields("PATH"));
		declarer.declareStream("StreamBolt3",new Fields("PATH"));
		declarer.declareStream("StreamBolt5",new Fields("PATH"));
		declarer.declareStream("StreamDbWriter", new Fields("result"));
		
	}
    
}


