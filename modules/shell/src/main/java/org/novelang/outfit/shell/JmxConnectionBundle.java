package org.novelang.outfit.shell;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
* Keeps a {@link javax.management.remote.JMXConnector} and its {@link javax.management.MBeanServerConnection}
* together because documentation says that {@link javax.management.remote.JMXConnector#getMBeanServerConnection()}
* may or may not return always the same instance.
*/
/*package*/  public class JmxConnectionBundle
{
public final JMXConnector connector ;
public final MBeanServerConnection connection ;

public JmxConnectionBundle(
    final JMXConnector connector,
    final MBeanServerConnection connection
) {
  this.connector = checkNotNull( connector ) ;
  this.connection = checkNotNull( connection ) ;
}
}
