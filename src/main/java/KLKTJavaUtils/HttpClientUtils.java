package KLKTJavaUtils;


import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.klkt.klktkotlin.utils.*;

public class HttpClientUtils {
    public static HashSet<Integer> ACCEPT_STATUS_EXT = new HashSet();

    public HttpClientUtils() {
    }

    public static final Response getSkipErr(int timeOut, String uri, Map<String, String> headers) {
        try {
            return request(timeOut, true, uri, HttpClientUtils.HttpMethod.GET, headers, (String)null);
        } catch (Exception var4) {
            return HttpClientUtils.Response.buildException(uri, var4);
        }
    }

    public static final Response get(int timeOut, String uri, Map<String, String> headers) throws Exception {
        return request(timeOut, false, uri, HttpClientUtils.HttpMethod.GET, headers, (String)null);
    }

    public static final Response postSkipErr(int timeOut, String uri, Map<String, String> headers, String body) {
        try {
            return request(timeOut, true, uri, HttpClientUtils.HttpMethod.POST, headers, body);
        } catch (Exception var5) {
            return HttpClientUtils.Response.buildException(uri, var5);
        }
    }

    public static final Response post(int timeOut, String uri, Map<String, String> headers, String body) throws Exception {
        return request(timeOut, false, uri, HttpClientUtils.HttpMethod.POST, headers, body);
    }

    private static final Response request(int timeOut, boolean skipError, String uri, HttpMethod method, Map<String, String> headers, String body) throws Exception {
        HttpURLConnection conn = null;
        OutputStream os = null;

        Response var19;
        try {
            URL url = new URL(uri);
            if (uri.toLowerCase().startsWith("https")) {
                HttpsURLConnection httpsConnect = (HttpsURLConnection)url.openConnection();
                httpsConnect.setHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
                conn = httpsConnect;
            } else {
                conn = (HttpURLConnection)url.openConnection();
            }

            ((HttpURLConnection)conn).setDoOutput(true);
            ((HttpURLConnection)conn).setConnectTimeout(timeOut);
            ((HttpURLConnection)conn).setReadTimeout(timeOut);
            ((HttpURLConnection)conn).setRequestMethod(method.toString());
            if (method == HttpClientUtils.HttpMethod.POST) {
                ((HttpURLConnection)conn).setRequestProperty("accept", "*/*");
                ((HttpURLConnection)conn).setRequestProperty("Content-type", "application/json;charset=UTF-8");
            }

            if (headers != null) {
                Iterator var17 = headers.entrySet().iterator();

                while(var17.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry)var17.next();
                    ((HttpURLConnection)conn).setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                }
            }

            if (!StringUtils.isEmpty(body)) {
                os = ((HttpURLConnection)conn).getOutputStream();
                os.write(body.getBytes());
                os.flush();
            }

            int responseCode = ((HttpURLConnection)conn).getResponseCode();
            if (!ACCEPT_STATUS_EXT.contains(responseCode)) {
                if (skipError) {
                    var19 = HttpClientUtils.Response.build(uri, responseCode, ((HttpURLConnection)conn).getHeaderFields(), streamToStr(((HttpURLConnection)conn).getErrorStream()));
                    return var19;
                }

                throw new Exception("Failed! HTTP status: " + responseCode + " " + (((HttpURLConnection)conn).getResponseMessage() == null ? "" : ((HttpURLConnection)conn).getResponseMessage()) + "\n" + streamToStr(((HttpURLConnection)conn).getErrorStream()));
            }

            var19 = HttpClientUtils.Response.build(uri, responseCode, ((HttpURLConnection)conn).getHeaderFields(), streamToStr(((HttpURLConnection)conn).getInputStream()));
        } catch (Exception var14) {
            if (skipError) {
                Response var9 = HttpClientUtils.Response.buildException(uri, var14);
                return var9;
            }

            throw var14;
        } finally {
            close(os, conn);
        }

        return var19;
    }

    private static String streamToStr(InputStream inputResponse) throws UnsupportedEncodingException {
        StringBuffer br = new StringBuffer();
        if (inputResponse != null) {
            try {
                BufferedInputStream bis = new BufferedInputStream(inputResponse);
                Throwable var3 = null;

                try {
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    Throwable var5 = null;

                    try {
                        for(int result = bis.read(); result != -1; result = bis.read()) {
                            buf.write((byte)result);
                        }

                        if (br.length() > 0) {
                            br.append(" ");
                        }

                        br.append(buf.toString("UTF-8"));
                    } catch (Throwable var30) {
                        var5 = var30;
                        throw var30;
                    } finally {
                        if (buf != null) {
                            if (var5 != null) {
                                try {
                                    buf.close();
                                } catch (Throwable var29) {
                                    var5.addSuppressed(var29);
                                }
                            } else {
                                buf.close();
                            }
                        }

                    }
                } catch (Throwable var32) {
                    var3 = var32;
                    throw var32;
                } finally {
                    if (bis != null) {
                        if (var3 != null) {
                            try {
                                bis.close();
                            } catch (Throwable var28) {
                                var3.addSuppressed(var28);
                            }
                        } else {
                            bis.close();
                        }
                    }

                }
            } catch (Exception var34) {
                System.out.println("getResponse error. " + var34);
            }
        } else {
            System.out.println("input stream null");
        }

        return br.toString();
    }

    private static <T> void close(Object... targets) {
        Object[] var1 = targets;
        int var2 = targets.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Object in = var1[var3];

            try {
                if (in != null) {
                    if (in instanceof HttpURLConnection) {
                        ((HttpURLConnection)in).disconnect();
                    } else {
                        ((AutoCloseable)in).close();
                    }
                }
            } catch (Exception var6) {
                System.out.println(var6);
            }
        }

    }

    static {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init((KeyManager[])null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException var2) {
            var2.printStackTrace();
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

        ACCEPT_STATUS_EXT.add(202);
        ACCEPT_STATUS_EXT.add(200);
    }

    public static final class Response {
        public final String uri;
        private final Map<String, List<String>> headers;
        private final String body;
        public final int status;

        private Response(String uri, int status, Map<String, List<String>> headers, String body) {
            this.uri = uri;
            this.status = status;
            this.body = body;
            this.headers = new HashMap();
            if (headers != null) {
                Iterator var5 = headers.entrySet().iterator();

                while(var5.hasNext()) {
                    Map.Entry<String, List<String>> entry = (Map.Entry)var5.next();
                    if (entry.getKey() != null && entry.getValue() != null && ((List)entry.getValue()).size() >= 1) {
                        this.headers.put(((String)entry.getKey()).toLowerCase(), entry.getValue());
                    }
                }
            }

        }

        public static Response build(String uri, int status, Map<String, List<String>> headers, String body) {
            return new Response(uri, status, headers, body);
        }

        public static Response buildException(String uri, Exception e) {
            String errStr = e.toString();
            if (!errStr.contains("java.net.UnknownHostException") && !errStr.contains("java.net.ConnectException: Connection refused") && !errStr.contains("java.net.NoRouteToHostException: No route to host")) {
                return errStr.contains("java.net.SocketTimeoutException") ? new Response(uri, 408, (Map)null, e.getMessage()) : new Response(uri, 500, (Map)null, KLKTStringUtils.Companion.writeOutException(e));
            } else {
                return new Response(uri, 503, (Map)null, errStr);
            }
        }

        public String getHeader(String lowerKey) {
            return this.getHeader(lowerKey, (String)null);
        }

        public String getHeader(String lowerKey, String defaultValue) {
            lowerKey = lowerKey.toLowerCase();
            return !this.headers.containsKey(lowerKey) ? defaultValue : (String)((List)this.headers.get(lowerKey)).get(0);
        }

        public Map<String, List<String>> headers() {
            return this.headers;
        }

        public KLKTJsonObject headersJs() {
            return KLKTJsonObject.build(this.headers);
        }

        public String bodyStr() {
            return this.body;
        }

        public KLKTJsonObject bodyJs() {
            return KLKTJsonObject.build(this.body);
        }

        public boolean isOk() {
            return HttpClientUtils.ACCEPT_STATUS_EXT.contains(this.status);
        }

        public boolean statusIs(int... status) {
            return Arrays.stream(status).anyMatch((i) -> {
                return i == this.status;
            });
        }
    }

    public static enum HttpMethod {
        POST,
        GET;

        private HttpMethod() {
        }
    }
}