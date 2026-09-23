package mci.vietnam.splam.web.config;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import mci.vietnam.splam.web.service.AddNumberService;

@Component
public class RequestBodyLimitFilter extends OncePerRequestFilter {
    public static final String BODY_TOO_LARGE_ATTRIBUTE =
        RequestBodyLimitFilter.class.getName() + ".bodyTooLarge";
    public static final int MAX_REQUEST_BODY_BYTES =
        AddNumberService.MAX_OPERAND_LENGTH * 2 + 32;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getContentLengthLong() > MAX_REQUEST_BODY_BYTES) {
            writeTooLargeResponse(response);
            return;
        }

        try {
            filterChain.doFilter(new LimitedBodyRequest(request), response);
        } catch (BodyLimitExceededException exception) {
            if (!response.isCommitted()) {
                writeTooLargeResponse(response);
            }
        }
    }

    private void writeTooLargeResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            "{\"code\":\"INPUT_TOO_LARGE\","
                + "\"message\":\"Request body exceeds the maximum allowed size.\"}");
    }

    private static final class LimitedBodyRequest extends HttpServletRequestWrapper {
        private ServletInputStream inputStream;

        private LimitedBodyRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (inputStream == null) {
                inputStream = new LimitedInputStream(super.getInputStream(), this);
            }
            return inputStream;
        }
    }

    private static final class LimitedInputStream extends ServletInputStream {
        private final ServletInputStream delegate;
        private final HttpServletRequest request;
        private long bytesRead;

        private LimitedInputStream(ServletInputStream delegate, HttpServletRequest request) {
            this.delegate = delegate;
            this.request = request;
        }

        @Override
        public int read() throws IOException {
            int value = delegate.read();
            if (bytesRead >= MAX_REQUEST_BODY_BYTES && value >= 0) {
                markExceededAndThrow();
            }
            if (value >= 0) {
                bytesRead++;
            }
            return value;
        }

        @Override
        public int read(byte[] bytes, int offset, int length) throws IOException {
            if (length == 0) {
                return 0;
            }
            int allowedLength = (int) Math.min(length, MAX_REQUEST_BODY_BYTES - bytesRead);
            if (allowedLength == 0) {
                int extraByte = delegate.read();
                if (extraByte >= 0) {
                    markExceededAndThrow();
                }
                return -1;
            }
            int count = delegate.read(bytes, offset, allowedLength);
            if (count > 0) {
                bytesRead += count;
            }
            return count;
        }

        private void markExceededAndThrow() throws BodyLimitExceededException {
            request.setAttribute(BODY_TOO_LARGE_ATTRIBUTE, Boolean.TRUE);
            throw new BodyLimitExceededException();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            delegate.setReadListener(readListener);
        }
    }

    private static final class BodyLimitExceededException extends IOException {
        private static final long serialVersionUID = 1L;
    }
}