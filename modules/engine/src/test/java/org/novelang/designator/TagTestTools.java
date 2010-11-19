package org.novelang.designator;

import org.fest.reflect.core.Reflection;

/**
 * Encapsulation violation of {@link Tag} class.
 * 
 * @author Laurent Caillette
 */
public class TagTestTools {

  private TagTestTools() { }

  public static String getTagAsString( final Tag tag ) {
    return Reflection.field( "name" ).ofType( String.class ).in( tag ).get() ;
  }

}
