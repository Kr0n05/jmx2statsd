jmx2statsd
==========

Send JMX numeric attibutes to StatsD
![alt tag](https://github.com/Kr0n05/jmx2statsd/blob/master/MEDIA/jmx2statsd.jpg)

# Let's get started!

### Clone repository:

```
git clone https://github.com/Kr0n05/jmx2statsd.git
```

### Build classes:

```
cd jmx2statsd/
ant build
```

### Build JAR:

```
jar cvfm jmx2statsd.jar META-INF/MANIFEST.MF *.class
```

# Ready to go!

### Example of running:

```
java -Xms256m -Xmx512m -DjmxHost=localhost -DstatsdHost=localhost -jar jmx2statsd.jar
```

### Available arguments

```
jmxHost - Default: localhost
jmxPort - Default: 7199

statsdHost - Default: localhost
statsdPort - Default: 8125

numThreads - Number of threads that will be gathering the information from JMX. Default: 8
refreshRate - Number of seconds between two JMX reads. Default: 60
```

