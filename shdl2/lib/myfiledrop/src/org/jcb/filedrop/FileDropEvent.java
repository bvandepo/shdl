package org.jcb.filedrop;

/**
 * This is the event that is passed to the
 * {@link FileDropListener#filesDropped filesDropped(...)} method in
 * your {@link FileDropListener} when files are dropped onto
 * a registered drop target.
 *
 * <p>I'm releasing this code into the Public Domain. Enjoy.
 * </p>
 * <p><em>Original author: Robert Harder, rharder@usa.net</em></p>
 *
 * @author  Robert Harder
 * @author  rharder@usa.net
 * @version 1.1
 */
public class FileDropEvent extends java.util.EventObject
{

    private java.io.File[] files;
    
    /**
     * Constructs a {@link FileDropEvent} with the array
     * of files that were dropped and the
     * {@link FileDropBean} that initiated the event.
     *
     * @param files The array of files that were dropped
     * @source The event source
     * @since 1.1
     */
    public FileDropEvent( java.io.File[] files, Object source )
    {   super( source );
        this.files = files;
    }   // end constructor
    
    /**
     * Returns an array of files that were dropped on a
     * registered drop target.
     *
     * @return array of files that were dropped
     * @since 1.1
     */
    public java.io.File[] getFiles()
    {   return files;
    }   // end getFiles

}   // end class FileDropEvent
