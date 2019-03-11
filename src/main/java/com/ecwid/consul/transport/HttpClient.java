package com.ecwid.consul.transport;

import com.ecwid.consul.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * @author shaojieyue
 * Created at 2019-03-11 10:50
 */
public class HttpClient {
    private int connectTimeout;
    private int readTimeout;

    public HttpClient(int connectTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public RawResponse sendingGetRequest(String urlString) throws IOException {
        return sendingRequest(urlString, "GET");
    }

    public RawResponse sendingPutRequest(String urlString,String content) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoInput(true);
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(content);
            out.flush();
            out.close();

            return handleResponse(con);
        }catch (IOException e){
            throw e;
        }finally {
            if (Objects.nonNull(con)) {
                con.disconnect();
            }
        }
    }

    public RawResponse sendingDeleteRequest(String urlString) throws IOException {
        return sendingRequest(urlString, "DELETE");
    }

    private RawResponse sendingRequest(String urlString, String requestMethod) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestMethod);
            con.setConnectTimeout(connectTimeout);
            con.setReadTimeout(readTimeout);
            return handleResponse(con);
        }catch (IOException e){
            throw e;
        }finally {
            if (Objects.nonNull(con)) {
                con.disconnect();
            }
        }
    }

    private RawResponse handleResponse(HttpURLConnection urlConnection) throws IOException {
        int statusCode = urlConnection.getResponseCode();
        String statusMessage = urlConnection.getResponseMessage();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        Long consulIndex = parseUnsignedLong(urlConnection.getHeaderField("X-Consul-Index"));
        Boolean consulKnownLeader = parseBoolean(urlConnection.getHeaderField("X-Consul-Knownleader"));
        Long consulLastContact = parseUnsignedLong(urlConnection.getHeaderField("X-Consul-Lastcontact"));

        return new RawResponse(statusCode, statusMessage, content.toString(), consulIndex, consulKnownLeader, consulLastContact);
    }

    private Boolean parseBoolean(String value) {
        if (value == null) {
            return null;
        }

        if ("true".equals(value)) {
            return true;
        }

        if ("false".equals(value)) {
            return false;
        }

        return null;
    }

    private Long parseUnsignedLong(String value) {
        if (value == null) {
            return null;
        }

        if (value == null) {
            return null;
        }

        try {
            return Utils.parseUnsignedLong(value);
        } catch (Exception e) {
            return null;
        }
    }
}
