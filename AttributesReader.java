import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;



public class AttributesReader implements Runnable {
	private ArrayList<AttributeValueHolder> attributes = new ArrayList<AttributeValueHolder>();;
	private MBeanServerConnection mbeanServerConnection;

	private StatsdClient statsd;
	
	public AttributesReader(MBeanServerConnection mbeanServerConnection){
		this.mbeanServerConnection = mbeanServerConnection;
		try {
			statsd = new StatsdClient(Config.statsDHost, Config.statsDPort);
		} catch (IOException e) {
			System.err.println("Com tin wong!");
		}
	}
	
    public void run() {
		String myName = Config.getJmxHost();
		if ( myName.equals("localhost") || myName.startsWith("127.") ) {
			try {
				myName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				myName = Config.getJmxHost();
			}
		}
    	
		while (true) {
			try {
				Thread.sleep(Config.getRefreshRate());
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

	    	for (AttributeValueHolder attribute : attributes) {
	    			try {
	    				
	    				ObjectName mbeanName = attribute.getMbean();
	    				
	    				Object attributeValue = mbeanServerConnection.getAttribute(attribute.getMbean(), attribute.getAttribute().getName());
	    				String statsName = getStatsName(myName, attribute, mbeanName);
	    				
	    				statsd.gauge(statsName, Double.parseDouble(attributeValue.toString()));
	    				
	    			} catch (Exception e) {}	
			}
		}
    }

    public void addAttribute(AttributeValueHolder attributeValueHolder){
    	this.attributes.add(attributeValueHolder);
    }

	public ArrayList<AttributeValueHolder> getAttributes() {
		return attributes;
	}
	
	public String getStatsName(String jmxHost, AttributeValueHolder attribute, ObjectName mbeanName){
		return "jmx." 
				+ jmxHost.replace(".", "_") + "_" +
				+ Config.getJmxPort()  + "." 
				+ mbeanName.toString().replace("\"", "").replace(".","_").replace(",",".").replace(":",".").replace("=", "") + "." 
				+ attribute.getAttribute().getName();
	}
 
}
