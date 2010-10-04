package org.novelang.outfit.shell;

import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import com.google.common.collect.ImmutableSet;

/**
 * Creates a properly-configured {@link JMXConnector}.
 *
 * @author Laurent Caillette
 */
public interface JmxKit
{
    /**
     * Creates the JMX connector.
     *
     * @return a non-null object.
     */
    JMXConnector createJmxConnector( String host, int port ) throws InterruptedException, IOException ;


}
