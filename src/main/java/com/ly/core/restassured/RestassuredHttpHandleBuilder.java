package com.ly.core.restassured;

import com.ly.core.exception.BizException;
import com.ly.core.utils.Utils;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

/**
 * rest assured http 处理构建类
 * @author luoy
 *
 */
public class RestassuredHttpHandleBuilder {

  private volatile static RestassuredHttpHandleBuilder builder = null;

  public static RestassuredHttpHandleBuilder create() {
    if (builder == null) {
      synchronized (RestassuredHttpHandleBuilder.class) {
        if (builder == null) {
          builder = new RestassuredHttpHandleBuilder();
        }
      }
    }
    return builder;
  }

  private RestassuredHttpHandleBuilder() {
  }

  public RestassuredHttpHandleBuilder setBaseUrl(String baseUrl) {
    RestAssured.baseURI = baseUrl;
    return this;
  }

  public Response get(String url, Filter...filters) {
    return get(null, url, filters);
  }

  public Response get(Map<String, ?> parameters, String url, Filter...filters) {
    return get(null, parameters, url, filters);
  }

  public Response get(Map<String, ?> headers, Map<String, ?> parameters, String url, Filter...filters) {
    return get(headers, parameters, url, 5000, 5000, 5000, true, filters);
  }

  public Response get(String url, int connectTimeout, int requestTimeout, int socketTimeout, boolean redirectsEnabled, Filter...filters) {
    return get(null, null, url, connectTimeout, requestTimeout, socketTimeout, redirectsEnabled, filters);
  }

  public Response get(Map<String, ?> parameters, String url, int connectTimeout, int requestTimeout, int socketTimeout, boolean redirectsEnabled, Filter...filters) {
    return get(null, parameters, url, connectTimeout, requestTimeout, socketTimeout, redirectsEnabled, filters);
  }

  /**
   * get请求
   * @param headers
   * @param parameters
   * @param url
   * @param connectTimeout  设置连接超时时间
   * @param requestTimeout  设置请求超时时间
   * @param socketTimeout
   * @param redirectsEnabled  默认允许自动重定向
   * @param filters  过滤器
   * @return
   */
  public Response get(Map<String, ?> headers, Map<String, ?> parameters, String url,
            int connectTimeout, int requestTimeout, int socketTimeout, boolean redirectsEnabled, Filter...filters) {
    config().httpClient(createConfig(connectTimeout, requestTimeout, socketTimeout, redirectsEnabled));

    RequestSpecification restHandle =  given();

    //default content-type 如果yaml中headers中存在content-type 将覆盖该配置
    restHandle.config(config().encoderConfig(EncoderConfig.encoderConfig().defaultCharsetForContentType("UTF-8", ContentType.URLENC)));

    if(headers != null ) {
      restHandle.headers(headers);
    }

    if(filters != null && filters.length > 0) {
      restHandle.filters(Arrays.asList(filters));
    }

    if (parameters != null ) {
      if (Utils.isExistBrace(url)) {
        // /order/{orderId}/{driverId}
        return restHandle.when().get(url, parameters);
      } else {
        // /order     {"orderId", "11111"} {"driverId", "22222"}
        restHandle.params(parameters);
        return restHandle.when().get(url);
      }
    } else {
      return restHandle.when().get(url);
    }
  }

  public Response put(Map<String, ?> parameters, String url, Filter...filters) {
    return put(null, parameters, url, filters);
  }

  public Response put(Map<String, ?> headers, Map<String, ?> parameters, String url, Filter...filters) {
    return put(headers, parameters, null, url, filters);
  }

  public Response put(Map<String, ?> headers, Map<String, ?> parameters, String contentType, String url, Filter...filters) {
    return put(headers, parameters, contentType, url, 5000, 5000, 5000, true, filters);
  }

  /**
   * put请求  默认json请求
   * @param headers
   * @param data
   * @param url
   * @param filters
   * @return
   */
  public Response put(Map<String, ?> headers, String data, String url, Filter...filters) {
    return put(headers, data, ContentType.JSON, url, 5000, 5000, 5000, true, filters);
  }

  /**
   * put请求 json或者xml模式
   * @param headers
   * @param data
   * @param contentType 只支持ContentType.JSON, ContentType.XML
   * @param url
   * @param filters
   * @return
   */
  public Response put(Map<String, ?> headers, String data, ContentType contentType, String url, Filter...filters) {
    return put(headers, data, contentType, url, 5000, 5000, 5000, true, filters);
  }

  /**
   *  put请求  json或者xml模式
   * @param headers
   * @param data
   * @param contentType  只支持ContentType.JSON, ContentType.XML
   * @param url
   * @param connectTimeout
   * @param requestTimeout
   * @param socketTimeout
   * @param redirectsEnabled
   * @param filters
   * @return
   */
  public Response put(Map<String, ?> headers, String data, ContentType contentType, String url,
            int connectTimeout, int requestTimeout, int socketTimeout, boolean redirectsEnabled, Filter...filters) {
    config().httpClient(createConfig(connectTimeout, requestTimeout, socketTimeout, redirectsEnabled));

    if (contentType != ContentType.JSON && contentType != ContentType.XML) {
      throw new BizException("content-type错误,只支持JSON,XML");
    }

    RequestSpecification restHandle =  given().contentType(contentType);

    if(headers != null ) {
      restHandle.headers(headers);
    }

    restHandle.body(data);

    if(filters != null && filters.length > 0) {
      restHandle.filters(Arrays.asList(filters));
    }
    return restHandle.when().put(url);
  }

  /**
   * eg.
   * parameters: http://127.0.0.1:3301/put/{id}/{orderId}      map: {"id", "1"} {"orderId", "2"}
   * url :http://127.0.0.1:3301/put/1/2
   *
   * eg.
   * parameters: http://127.0.0.1:3301/put      map: {"id", "1"} {"orderId", "2"}
   * url :http://127.0.0.1:3301/put?id=1&orderId=2
   * @param headers
   * @param parameters
   * @param url
   * @param connectTimeout
   * @param requestTimeout
   * @param socketTimeout
   * @param redirectsEnabled
   * @param filters
   * @return
   */
  public Response put(Map<String, ?> headers, Map<String, ?> parameters, String contentType, String url,
            int connectTimeout, int requestTimeout, int socketTimeout, boolean redirectsEnabled, Filter...filters) {
    config().httpClient(createConfig(connectTimeout, requestTimeout, socketTimeout, redirectsEnabled));

    RequestSpecification restHandle =  given();
    if (!StringUtils.isBlank(contentType)) {
      restHandle.config(config().encoderConfig(encoderConfig().defaultCharsetForContentType("UTF-8", ContentType.URLENC)))
          .contentType(contentType);
      restHandle.contentType(contentType);
    } else {
      restHandle.config(config().encoderConfig(EncoderConfig.encoderConfig().defaultCharsetForContentType("UTF-8", ContentType.URLENC)));
    }

    if(headers != null ) {
      restHandle.headers(headers);
    }

    if(filters != null && filters.length > 0) {
      restHandle.filters(Arrays.asList(filters));
    }

    if (parameters != null ) {
      if (Utils.isExistBrace(url)) {
        // /order/{orderId}/{driverId}
        return restHandle.when().put(url, parameters);
      } else {
        // /order     {"orderId", "11111"} {"driverId", "22222"}
        restHandle.params(parameters);
        return restHandle.when().put(url);
      }
    } else {
      return restHandle.when().put(url);
    }
  }

  public Response delete(Map<String, ?> parameters, String url, Filter...filters) {
    return delete(null, parameters, url, filters);
  }

  public Response delete(Map<String, ?> headers, Map<String, ?> parameters, String url, Filter...filters) {
    return delete(headers, parameters, url, 5000, 5000, 5000, true, filters);
  }

  /**
   * @param headers
   * @param parameters
   * @param url
   * @param connectTimeout
   * @param requestTimeout
   * @param socketTimeout
   * @param redirectsEnabled
   * @param filters
   * @return
   */
  public Response delete(Map<String, ?> headers, Map<String, ?> parameters, String url,
               int connectTimeout, int requestTimeout, int socketTimeout, boolean redirectsEnabled, Filter...filters) {
    config().httpClient(createConfig(connectTimeout, requestTimeout, socketTimeout, redirectsEnabled));

    RequestSpecification restHandle =  given()
        .config(config().encoderConfig(encoderConfig().defaultCharsetForContentType("UTF-8", ContentType.URLENC)));

    if(headers != null ) {
      restHandle.headers(headers);
    }

    if(filters != null && filters.length > 0) {
      restHandle.filters(Arrays.asList(filters));
    }

    if (parameters != null ) {
      if (Utils.isExistBrace(url)) {
        // /order/{orderId}/{driverId}
        return restHandle.when().delete(url, parameters);
      } else {
        // /order     {"orderId", "11111"} {"driverId", "22222"}
        restHandle.params(parameters);
        return restHandle.when().delete(url);
      }
    } else {
      return restHandle.when().delete(url);
    }
  }

  public Response post(Map<String, ?> headers, Map<String, ?> parameters, String url, Filter...filters) {
    return post(headers, parameters, null, url, filters);
  }

  public Response post(Map<String, ?> headers, Map<String, ?> parameters, String contentType, String url, Filter...filters) {
    return post(headers, parameters, contentType, url,5000, 5000, 5000, true, filters);
  }

  /**
   * post请求表单模式
   * @param headers
   * @param parameters
   * @param contentType
   * @param url
   * @param connectTimeout  设置连接超时时间
   * @param requestTimeout  设置请求超时时间
   * @param socketTimeout
   * @param redirectsEnabled  是否允许自动重定向
   * @param filters  过滤器
   * @return
   */
  public Response post(Map<String, ?> headers, Map<String, ?> parameters, String contentType, String url,
             int connectTimeout, int requestTimeout, int socketTimeout, boolean redirectsEnabled, Filter...filters) {
    config().httpClient(createConfig(connectTimeout, requestTimeout, socketTimeout, redirectsEnabled));

    RequestSpecification restHandle =  given();
    if (!StringUtils.isBlank(contentType)) {
      restHandle.config(config().encoderConfig(encoderConfig().defaultCharsetForContentType("UTF-8", ContentType.URLENC)))
          .contentType(contentType);
    } else {
      restHandle.config(config().encoderConfig(EncoderConfig.encoderConfig().defaultCharsetForContentType("UTF-8", ContentType.URLENC)));
    }

    if(headers != null ) {
      restHandle.headers(headers);
    }

    if(parameters != null ) {
      restHandle.formParams(parameters);
    }

    if(filters != null && filters.length > 0) {
      restHandle.filters(Arrays.asList(filters));
    }
    return restHandle.when().post(url);
  }

  /**
   * post请求 默认json请求
   * @param data
   * @param url
   * @param filters
   * @return
   */
  public Response post(String data, String url, Filter...filters) {
    return post(null, data ,url, filters);
  }

  /**
   * post请求 json或者xml模式
   * @param headers
   * @param data
   * @param contentType 只支持ContentType.JSON, ContentType.XML
   * @param url
   * @param filters
   * @return
   */
  public Response post(Map<String, ?> headers, String data, ContentType contentType, String url, Filter...filters) {
    return post(headers, data, contentType, url, 5000, 5000, 5000, true, filters);
  }

  /**
   * post请求 默认json请求
   * @param headers
   * @param data
   * @param url
   * @param filters
   * @return
   */
  public Response post(Map<String, ?> headers, String data, String url, Filter...filters) {
    return post(headers, data, ContentType.JSON, url, 5000, 5000, 5000, true, filters);
  }

  /**
   * post请求，入参为json或者xml
   * @param headers
   * @param data
   * @param url
   * @return
   */
  public Response post(Map<String, ?> headers, String data, ContentType contentType, String url, int connectTimeout, int requestTimeout, int socketTimeout,
             boolean redirectsEnabled, Filter...filters) {
    config().httpClient(createConfig(connectTimeout, requestTimeout, socketTimeout, redirectsEnabled));

    if (contentType != ContentType.JSON && contentType != ContentType.XML) {
      throw new BizException("content-type错误,只支持JSON,XML");
    }

    RequestSpecification restHandle =  given().contentType(contentType);

    if(headers != null ) {
      restHandle.headers(headers);
    }

    if(data != null ) {
      restHandle.body(data);
    }

    if(filters != null && filters.length > 0) {
      restHandle.filters(Arrays.asList(filters));
    }

    return restHandle.when().post(url);
  }

  private HttpClientConfig createConfig(int connectTimeout, int requestTimeout, int socketTimeout, boolean redirectsEnabled) {
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(connectTimeout)
        .setConnectionRequestTimeout(requestTimeout)
        .setSocketTimeout(socketTimeout)
        .setRedirectsEnabled(redirectsEnabled)
        .build();

    return HttpClientConfig.httpClientConfig().httpClientFactory(() ->
        HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build());
  }

  /**
   * sslConfig设置
   */
  private void sslConfig(String certPath, String password) {
    KeyStore keyStore = null;
    SSLConfig config = null;

    try {
      keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(
          new FileInputStream(certPath),
          password.toCharArray());

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    if (!Objects.isNull(keyStore)) {
      org.apache.http.conn.ssl.SSLSocketFactory clientAuthFactory = null;
      try {
        clientAuthFactory = new org.apache.http.conn.ssl.SSLSocketFactory(keyStore, password);
      } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException e) {
        e.printStackTrace();
      }
      config = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();
    }
    RestAssured.config = RestAssured.config().sslConfig(config);
  }

  /**
   * ssl忽略设置
   * @param restHandle
   */
  private void relaxedSSL(RequestSpecification restHandle) {
    RestAssured.useRelaxedHTTPSValidation();
  }
}
