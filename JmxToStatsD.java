import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JmxToStatsD {
	
	public static Config config = new Config();
	public static ArrayList<JMXConnector> openJMXConnectors = new ArrayList<JMXConnector>();
	
	public static MBeanServerConnection getNewMBeanServerConnection() throws Exception{
		JMXServiceURL url = new JMXServiceURL(Config.getServiceURL());
		JMXConnector jmxConnector = JMXConnectorFactory.connect(url);
		openJMXConnectors.add(jmxConnector);
		
		MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
		return mbeanServerConnection;
	}
	
	public static void closeOpenJMXConnectors() throws IOException{
		for(JMXConnector jmxConnector : openJMXConnectors){
			jmxConnector.close();
		}
	}

	public static void main(String[] args){

		try {
			System.out.println("Connecting to JMX host " + Config.jmxHost + " on port " + Config.jmxPort + ".");
			MBeanServerConnection mbeanServerConnection = getNewMBeanServerConnection();
			Set<ObjectName> mbeans = mbeanServerConnection.queryNames(null,null);
			ArrayList<AttributeValueHolder> attributesList = new ArrayList<AttributeValueHolder>();

			System.out.println("Reading numeric attributes from available MBeans...");
			for (ObjectName mbean : mbeans) {
				MBeanAttributeInfo[] attributes = mbeanServerConnection.getMBeanInfo(mbean).getAttributes();
				for (MBeanAttributeInfo attribute : attributes) {
					if (Config.getAttributetypes().contains(attribute.getType())) {
						attributesList.add(new AttributeValueHolder(mbean, attribute));
					}
				}
			}
			System.out.println("Number of available attributes is " + attributesList.size() + ".");
			
			int numberOfAttributesPerThread = attributesList.size() / Config.getNumberOfThreads() + 1;
			System.out.println("Dividing attributes to " + Config.getNumberOfThreads() + " different attribute readers.");
			
			ArrayList<AttributesReader> attributeReaders = new ArrayList<AttributesReader>();
			attributeReaders.add(new AttributesReader(getNewMBeanServerConnection()));

			int attributesPerThreadAdded = 0;
			for (int j = 0; j < attributesList.size(); j++) {
				if (attributesPerThreadAdded == numberOfAttributesPerThread) {
					attributeReaders.add(new AttributesReader(getNewMBeanServerConnection()));
					attributesPerThreadAdded = 0;
				}
				attributeReaders.get(attributeReaders.size() - 1).addAttribute(attributesList.get(j));
				attributesPerThreadAdded++;
			}
			
			System.out.println("Sending values to " + Config.getStatsDHost());
			ArrayList<Thread> threads = new ArrayList<Thread>();
			for (int k = 0; k < attributeReaders.size(); k++) {
				Thread t = new Thread(attributeReaders.get(0));
				threads.add(t);
				t.start();
			}
			
			for(Thread thread : threads){
				thread.join();
			}

			closeOpenJMXConnectors();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
