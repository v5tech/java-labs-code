package org.v5tech.http.client;


import cn.hutool.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HttpGetRequestWithBody {

    public static void main(String[] args) {
        String URL = "https://httpbin.org/anything";

        JSONObject body = new JSONObject();
        body.put("clientId", "cli_a692154013e6900c");
        body.put("clientSecret", "pFFaSoB54jJivS8tCVy7CcAmoEJADIeS");
        try {
            StringEntity entityValue = new StringEntity(body.toString());
            CloseableHttpClient client = HttpClients.createDefault();

            HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity(URL);
            httpGetWithEntity.setEntity(entityValue);
            httpGetWithEntity.setHeader("Accept", "application/json");
            httpGetWithEntity.setHeader("Content-type", "application/json");

            CloseableHttpResponse responseClient = client.execute(httpGetWithEntity);

            HttpEntity entityResponse = responseClient.getEntity();
            String response = IOUtils.toString(entityResponse.getContent(), StandardCharsets.UTF_8);
            System.out.println(response);
            client.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}

class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {

    public HttpGetWithEntity() {
        super();
    }

    public HttpGetWithEntity(URI uri) {
        super();
        setURI(uri);
    }

    public HttpGetWithEntity(String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return HttpGet.METHOD_NAME;
    }

}
