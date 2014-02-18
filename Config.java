import java.util.HashSet;


public class Config {
	public static String jmxHost 		= System.getProperty("jmxHost", "localhost");
	public static int jmxPort 			= Integer.parseInt(System.getProperty("jmxPort", "7199"));
	
	public static String statsDHost 	= System.getProperty("statsdHost", "localhost");
	public static int statsDPort 		= Integer.parseInt(System.getProperty("statsDPort", "8125"));

	public static String serviceURL 	= "service:jmx:rmi:///jndi/rmi://" + jmxHost + ":" + jmxPort+ "/jmxrmi";
	public static int numberOfThreads 	= Integer.parseInt(System.getProperty("numThreads", "8"));
	
	public static int refreshRate 		= Integer.parseInt(System.getProperty("refreshRate", "60")) * 1000;

	public static final HashSet<String> attributeTypes = new HashSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("int");
			add("long");
			add("double");
			add("float");
			add("java.lang.Integer");
			add("java.lang.Long");
			add("java.lang.Double");
			add("java.lang.Float");
		}
	};
	
	public static HashSet<String> getAttributetypes() {
		return attributeTypes;
	}
	
	public static String getServiceURL() {
		return serviceURL;
	}
	
	public static int getNumberOfThreads() {
		return numberOfThreads;
	}
	
	public static int getJmxPort() {
		return jmxPort;
	}
	
	public static String getJmxHost() {
		return jmxHost;
	}
	
	public static int getStatsDPort() {
		return statsDPort;
	}
	
	public static String getStatsDHost() {
		return statsDHost;
	}
	
	public static int getRefreshRate() {
		return refreshRate;
	}
	
	
}
