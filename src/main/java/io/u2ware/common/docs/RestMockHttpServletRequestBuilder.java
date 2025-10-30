package io.u2ware.common.docs;

import java.net.URI;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.Mergeable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.request.ConfigurableSmartRequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;

public class RestMockHttpServletRequestBuilder implements ConfigurableSmartRequestBuilder<MockHttpServletRequestBuilder>, Mergeable {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private MockHttpServletRequestBuilder builder;

    public RestMockHttpServletRequestBuilder(MockHttpServletRequestBuilder builder){
        this.builder = builder;
    }

    @Override
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        return builder.postProcessRequest(request);
    }

    @Override
    public MockHttpServletRequest buildRequest(ServletContext servletContext) {
        return builder.buildRequest(servletContext);
    }

    @Override
    public boolean isMergeEnabled() {
        return builder.isMergeEnabled();
    }

    @Override
    public Object merge(Object arg0) {
        return builder.merge(arg0);
    }

    @Override
    public MockHttpServletRequestBuilder with(RequestPostProcessor requestPostProcessor) {
        return builder.with(requestPostProcessor);
    }

    ///////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////
    public RestMockHttpServletRequestBuilder with(Consumer<MockHttpServletRequest> processor) throws Exception{
        builder.with((request)->{
            processor.accept(request);
            return request;
        });
        return this;
    }
    public MockHttpServletRequestBuilder req() {
        return builder;
    }
    public MockHttpServletRequestBuilder build() {
        return builder;
    }
    public MockHttpServletRequestBuilder builder() {
        return builder;
    }

    ///////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////
    public static RestMockHttpServletRequestBuilder get(URI uri) throws Exception{
        return new RestMockHttpServletRequestBuilder(MockMvcRequestBuilders.get(uri));
    }
    public static RestMockHttpServletRequestBuilder post(URI uri) throws Exception{
        return new RestMockHttpServletRequestBuilder(MockMvcRequestBuilders.post(uri));
    }
    public static RestMockHttpServletRequestBuilder put(URI uri) throws Exception{
        return new RestMockHttpServletRequestBuilder(MockMvcRequestBuilders.put(uri));
    }
    public static RestMockHttpServletRequestBuilder patch(URI uri) throws Exception{
        return new RestMockHttpServletRequestBuilder(MockMvcRequestBuilders.patch(uri));
    }
    public static RestMockHttpServletRequestBuilder delete(URI uri) throws Exception{
        return new RestMockHttpServletRequestBuilder(MockMvcRequestBuilders.delete(uri));
    }
    public static RestMockHttpServletRequestBuilder multipart(URI uri) throws Exception{
        return new RestMockHttpServletRequestBuilder(MockMvcRequestBuilders.multipart(uri));
    }


    ///////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////
    public RestMockHttpServletRequestBuilder file(Supplier<MultipartFile[]> content) throws Exception {
        return file(content.get());
    }
    public <T> RestMockHttpServletRequestBuilder file(Function<T,MultipartFile[]> content, T t) throws Exception{
        return file(content.apply(t));
    }
    public <T,U> RestMockHttpServletRequestBuilder file(BiFunction<T,U,MultipartFile[]> content, T t, U u) throws Exception{
        return file(content.apply(t,u));
    }
    public RestMockHttpServletRequestBuilder file(MultipartFile... files) throws Exception{
        return with((request)->{
            if(request instanceof MockMultipartHttpServletRequest mockMultipartHttpServletRequest) {
                for(MultipartFile file : files){
                    mockMultipartHttpServletRequest.addFile(file);
                }
            }
        });        
    }


    ///////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////
    public <R> RestMockHttpServletRequestBuilder content(Supplier<R> content) throws Exception {
        return content(content.get());
    }
    public <T,R> RestMockHttpServletRequestBuilder content(Function<T,R> content, T t) throws Exception{
        return content(content.apply(t));
    }
    public <T,U,R> RestMockHttpServletRequestBuilder content(BiFunction<T,U,R> content, T t, U u) throws Exception{
        return content(content.apply(t,u));
    }
    public RestMockHttpServletRequestBuilder content(Object content) throws Exception{
        final byte[] contents = objectMapper.writeValueAsBytes(content);
        return with((request)->{
            request.setContentType(MediaType.APPLICATION_JSON.toString());
            request.setContent(contents);
        });
    }

    public RestMockHttpServletRequestBuilder param(String key, String value) throws Exception{
        return with((request)->{
            request.addParameter(key, value);
        });
    }
    public RestMockHttpServletRequestBuilder header(String key, String value) throws Exception{
        return with((request)->{
            request.addHeader(key, value);
        });
    }


    //////////////////////////////////////////////////
    // 
    //////////////////////////////////////////////////
    public <R> RestMockHttpServletRequestBuilder auth(Supplier<R> content) throws Exception{
        return auth(content.get());
    }
    public <T,R> RestMockHttpServletRequestBuilder auth(Function<T,R> content, T t) throws Exception{
        return auth(content.apply(t));
    }
    public <T,U,R> RestMockHttpServletRequestBuilder auth(BiFunction<T,U,R> content, T t, U u) throws Exception{
        return auth(content.apply(t, u));
    }
    public RestMockHttpServletRequestBuilder auth(Object value) throws Exception{
        if(value instanceof Jwt jwt) {
            builder.with(RestSecurityRequestPostProcessorBuilder.auth(jwt));
        }else{
            builder.with(RestSecurityRequestPostProcessorBuilder.auth(value.toString()));
        }
        return this;
    }

    //////////////////////////////////////////////////
    // 
    //////////////////////////////////////////////////
    public <R> RestMockHttpServletRequestBuilder security(Supplier<R> content) throws Exception{
        return security(content.get());
    }
    public <T,R> RestMockHttpServletRequestBuilder security(Function<T,R> content, T t) throws Exception{
        return security(content.apply(t));
    }
    public <T,U,R> RestMockHttpServletRequestBuilder security(BiFunction<T,U,R> content, T t, U u) throws Exception{
        return security(content.apply(t, u));
    }
    public RestMockHttpServletRequestBuilder security(Object value) throws Exception{
        if(value instanceof Jwt jwt) {
            builder.with(RestSecurityRequestPostProcessorBuilder.security(jwt));
        }else{
            builder.with(RestSecurityRequestPostProcessorBuilder.security(value.toString()));
        }
        return this;
    }
}