package org.novelang.outfit.shell;

import javax.management.ObjectName;

import org.novelang.outfit.shell.JmxConnectionBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
* @author Laurent Caillette
*/
/*package*/ class JmxBeanValue
{
  private final ObjectName objectName ;
  private final Object jmxBean;
  private final JmxConnectionBundle connectionBundle ;

  public JmxBeanValue(
      final JmxConnectionBundle connectionBundle,
      final ObjectName objectName,
      final Object jmxBean
  ) {
    this.connectionBundle = checkNotNull( connectionBundle ) ;
    this.objectName = checkNotNull( objectName ) ;
    this.jmxBean = checkNotNull( jmxBean ) ;
  }

  public ObjectName getObjectName() {
    return objectName ;
  }

  public Object getJmxBean() {
    return jmxBean ;
  }

  public JmxConnectionBundle getConnectionBundle() {
    return connectionBundle;
  }
}
