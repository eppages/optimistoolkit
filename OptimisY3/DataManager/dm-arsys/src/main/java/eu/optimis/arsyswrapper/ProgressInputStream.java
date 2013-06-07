package eu.optimis.arsyswrapper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.lang.StringUtils;

public class ProgressInputStream extends InputStream
{
    private final long size;
    private long progress, lastUpdate = 0;
    private final InputStream inputStream;
    private final String name;
    private boolean closed = false;

    public ProgressInputStream(String name, InputStream inputStream, long size) {
        this.size = size;
        this.inputStream = inputStream;
        this.name = name;
    }

   public int getProgress()
    {
     double percentage = 100.0 * (  (double)progress / (double)size );
      return (int) Math.round(Math.floor(percentage));
    }

    public ProgressInputStream(String name, FileContent content)
    throws FileSystemException {
        this.size = content.getSize();
        this.name = name;
        this.inputStream = content.getInputStream();
    }

    @Override
    public void close() throws IOException {
        progress = size;
        System.out.print("\rProgress: " + getProgress() + "%" );
        System.out.println("");
        super.close();
        if (closed) throw new IOException("already closed");
        closed = true;
    }

    @Override
    public int read() throws IOException {
        int count = inputStream.read();
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(name, progress, lastUpdate, size);
        return count;
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(name, progress, lastUpdate, size);
        return count;
    }

    long maybeUpdateDisplay(String name, long progress, long lastUpdate, long size)
     {
        if( (progress - lastUpdate > 1024 * 10) || (progress == lastUpdate))
         {
            lastUpdate = progress;
            System.out.print("\rProgress: " + getProgress() + "%" );
            System.out.flush();
        }
        return lastUpdate;
    }
}

