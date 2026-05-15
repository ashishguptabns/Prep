package com.example.rackapp.filter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class GzipCompressionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String acceptEncoding = httpRequest.getHeader("Accept-Encoding");
            if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
                GzipResponseWrapper wrappedResponse = new GzipResponseWrapper(httpResponse);
                wrappedResponse.setHeader("Content-Encoding", "gzip");
                try {
                    chain.doFilter(request, wrappedResponse);
                } finally {
                    wrappedResponse.finish();
                }
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private static final class GzipResponseWrapper extends HttpServletResponseWrapper {

        private GzipServletOutputStream gzipOutputStream;
        private PrintWriter writer;

        public GzipResponseWrapper(HttpServletResponse response) throws IOException {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }
            if (gzipOutputStream == null) {
                gzipOutputStream = new GzipServletOutputStream(getResponse().getOutputStream());
            }
            return gzipOutputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (gzipOutputStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }
            if (writer == null) {
                gzipOutputStream = new GzipServletOutputStream(getResponse().getOutputStream());
                writer = new PrintWriter(new OutputStreamWriter(gzipOutputStream, getResponse().getCharacterEncoding()), true);
            }
            return writer;
        }

        @Override
        public void setContentLength(int len) {
            // Content length is unknown after compression.
        }

        public void finish() throws IOException {
            if (writer != null) {
                writer.close();
            }
            if (gzipOutputStream != null) {
                gzipOutputStream.close();
            }
        }
    }

    private static final class GzipServletOutputStream extends ServletOutputStream {

        private final GZIPOutputStream gzipOutputStream;

        public GzipServletOutputStream(ServletOutputStream outputStream) throws IOException {
            this.gzipOutputStream = new GZIPOutputStream(outputStream);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // No-op: synchronous response handling.
        }

        @Override
        public void write(int b) throws IOException {
            gzipOutputStream.write(b);
        }

        @Override
        public void flush() throws IOException {
            gzipOutputStream.flush();
        }

        @Override
        public void close() throws IOException {
            gzipOutputStream.finish();
            gzipOutputStream.close();
        }
    }
}
