package it.cleverad.engine.config.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // Read the body from the request and cache it
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = requestInputStream.readAllBytes(); // Java 9+ (or you can use a loop to read bytes in older versions)
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream();
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }

    private class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public CachedBodyServletInputStream() {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}