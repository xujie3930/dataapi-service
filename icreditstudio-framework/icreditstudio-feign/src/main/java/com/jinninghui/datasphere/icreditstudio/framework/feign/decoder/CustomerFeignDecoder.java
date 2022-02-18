package com.jinninghui.datasphere.icreditstudio.framework.feign.decoder;

import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.CommonOuterResponse;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpMessageConverterExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author liyanhui
 */
public class CustomerFeignDecoder implements Decoder {
    private ObjectFactory<HttpMessageConverters> messageConverters;

    private static final Logger logger = LoggerFactory.getLogger(CustomerFeignDecoder.class);

    public CustomerFeignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public Object decode(final Response response, Type type) throws IOException, FeignException {
        if (!(type instanceof Class) && !(type instanceof ParameterizedType) && !(type instanceof WildcardType)) {
            throw new DecodeException("type is not an instance of Class or ParameterizedType: " + type);
        } else {
            HttpMessageConverterExtractor<?> extractor = new HttpMessageConverterExtractor<>(type, this.messageConverters.getObject().getConverters());
            return decode0(extractor.extractData(new FeignResponseAdapter(response)));
        }
    }

    private Object decode0(Object result){
        if(result != null) {
            if (result instanceof CommonOuterResponse) {
                CommonOuterResponse commonOuterResponse = (CommonOuterResponse) result;
                if (!commonOuterResponse.isSuccess()) {
                    logger.error("Feign invoke error, {}", commonOuterResponse);
                    throw new AppException(commonOuterResponse.getReturnCode(), commonOuterResponse.getReturnMsg());
                }
                return commonOuterResponse;
            } else if (result instanceof BusinessResult) {
                BusinessResult businessResult = (BusinessResult) result;
                if (!businessResult.isSuccess()) {
                    logger.error("Feign invoke error, {}", businessResult);
                    throw new AppException(businessResult.getReturnCode(), businessResult.getReturnMsg());
                }
                return businessResult;
            } else {
                return result;
            }
        } else {
            return null;
        }
    }

    private class FeignResponseAdapter implements ClientHttpResponse {
        private final Response response;

        private FeignResponseAdapter(Response response) {
            this.response = response;
        }

        public HttpStatus getStatusCode() throws IOException {
            return HttpStatus.valueOf(this.response.status());
        }

        public int getRawStatusCode() throws IOException {
            return this.response.status();
        }

        public String getStatusText() throws IOException {
            return this.response.reason();
        }

        public void close() {
            try {
                this.response.body().close();
            } catch (IOException e) {
            }

        }

        public InputStream getBody() throws IOException {
            return this.response.body().asInputStream();
        }

        public HttpHeaders getHeaders() {
            return getHttpHeaders(this.response.headers());
        }

        HttpHeaders getHttpHeaders(Map<String, Collection<String>> headers) {
            HttpHeaders httpHeaders = new HttpHeaders();

            for (Map.Entry<String, Collection<String>> stringCollectionEntry : headers.entrySet()) {
                httpHeaders.put(stringCollectionEntry.getKey(), new ArrayList<>(stringCollectionEntry.getValue()));
            }

            return httpHeaders;
        }


    }
}