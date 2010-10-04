package org.novelang.outfit.shell;

import javax.management.ObjectName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
* @author Laurent Caillette
*/
/*package*/ class JmxBeanKey {

  private final ObjectName objectName ;
  private final JmxKit jmxKit ;

  public JmxBeanKey( final ObjectName objectName, final JmxKit jmxKit ) {
    this.objectName = checkNotNull( objectName ) ;
    this.jmxKit = checkNotNull( jmxKit ) ;
  }

  @Override
  public boolean equals( final Object other ) {
    if( this == other ) {
        return true ;
    }
    if( other == null || getClass() != other.getClass() ) {
      return false ;
    }

    final JmxBeanKey that = ( JmxBeanKey ) other ;

    if( !jmxKit.equals( that.jmxKit ) ) {
      return false ;
    }
    if( !objectName.equals( that.objectName ) ) {
      return false ;
    }
    return true ;
  }

  @Override
  public int hashCode() {
    int result = objectName.hashCode() ;
    result = 31 * result + jmxKit.hashCode() ;
    return result ;
  }
}
