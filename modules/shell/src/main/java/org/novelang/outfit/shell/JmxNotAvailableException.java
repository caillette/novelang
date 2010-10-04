package org.novelang.outfit.shell;

/**
 * Thrown when a {@link JavaShell} configured with no JMX has to use a feature that requires JMX.
 *
 * @author Laurent Caillette
 */
public class JmxNotAvailableException extends RuntimeException
{
}
