package storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Output Writer Bolt
* Bolt to collect the end result
*/
public class BoltFinalOutput extends BaseRichBolt {
    
	private static final long serialVersionUID = 7949540834062031472L;
	public static final Logger LOG = LoggerFactory.getLogger(BoltDecider.class);
	private OutputCollector objCollector;
	
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
			
		objCollector= collector;
				
	}	

    @Override
    public void execute(Tuple tuple) { 	
    	
    	
    	
    }


	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {		
			
	}
   
}


