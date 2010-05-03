package novelang.designator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.fest.reflect.core.Reflection;

import java.util.Set;

/**
 * Encapsulation violation of {@link Tag} class.
 * 
 * @author Laurent Caillette
 */
public class TagTestTools {
  
  public static String getTagAsString( final Tag tag ) {
    return Reflection.field( "name" ).ofType( String.class ).in( tag ).get() ;
  }

}
