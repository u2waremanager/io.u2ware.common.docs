package io.u2ware.common.docs;

import static org.springframework.core.GenericTypeResolver.resolveTypeArgument;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matchers;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;


public abstract class MockMvcRestDocs {

    protected Log logger = LogFactory.getLog(getClass());

    protected final Class<?> INTERESTED_TYPE = resolveTypeArgument(getClass(), MockMvcRestDocs.class);

    //////////////////////////////////////////////
    //
    //////////////////////////////////////////////
    protected Map<String,DocumentContext> documentContexts = new HashMap<>();

    public void putJson(String key, DocumentContext documentContext) {
        documentContexts.put(key, documentContext);
    }

    public <X> X getJson(String key) {
        return getJson(key, "$");
    }
    @SuppressWarnings("unchecked")
    public <X> X getJson(String key, String jsonPath) {
        try{
            DocumentContext document = documentContexts.get(key);
            return (X)document.read(jsonPath);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }



    //////////////////////////////////////////////
    //
    //////////////////////////////////////////////
    public Integer randomInt(){
        double dValue = Math.random();
        return (int)(dValue * 100);
    }
    public String randomText(String prefix){
        return prefix+randomInt();
    }


    //////////////////////////////////////////////
    //
    //////////////////////////////////////////////
    // @Autowired
    // private ObjectMapper objectMapper;
    
    // public URI uri(String uri, Object content){
    //     Map<String, Object> vars = objectMapper.convertValue(content, new TypeReference<Map<String, Object>>() {});
    //     return UriComponentsBuilder.fromUriString(uri).buildAndExpand(vars).encode().toUri();
    // }


    public URI uri(String uri){
        try{
            String[] elements = StringUtils.delimitedListToStringArray(uri, ".");

            if(elements.length > 1) {
                String key = elements[0];
                if(documentContexts.containsKey(key)){
                    // System.err.print("key: "+key);
                    // System.err.print("uri: "+uri);
                    String path = uri.replaceAll(key, "\\$");
                    // System.err.print("path: "+path);
                    String read = getJson(key, path);
                    // System.err.print("url: "+read);
                    return UriComponentsBuilder.fromUriString(read).build().toUri();
                }
            }
            return UriComponentsBuilder.fromUriString(uri).build().encode().toUri();

        }catch(Exception e) {
            e.printStackTrace();
            return UriComponentsBuilder.fromUriString(uri).build().encode().toUri();
        }
    }




    //////////////////////////////////////////////
    //
    //////////////////////////////////////////////
    public static RestMockHttpServletRequestBuilder GET(Function<String, URI> func, String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.get(func.apply(uri));
    }
    public static RestMockHttpServletRequestBuilder POST(Function<String, URI> func, String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.post(func.apply(uri)).content(new HashMap<>());
    }
    public static RestMockHttpServletRequestBuilder PUT(Function<String, URI> func, String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.put(func.apply(uri)).content(new HashMap<>());
    }
    public static RestMockHttpServletRequestBuilder PATCH(Function<String, URI> func, String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.patch(func.apply(uri)).content(new HashMap<>());
    }
    public static RestMockHttpServletRequestBuilder DELETE(Function<String, URI> func, String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.delete(func.apply(uri));
    }     
    public static RestMockHttpServletRequestBuilder MULTIPART(Function<String, URI> func, String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.multipart(func.apply(uri));
    }

    public static RestMockHttpServletRequestBuilder GET(String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.get(new URI(uri));
    }
    public static RestMockHttpServletRequestBuilder POST(String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.post(new URI(uri)).content(new HashMap<>());
    }
    public static RestMockHttpServletRequestBuilder PUT(String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.put(new URI(uri)).content(new HashMap<>());
    }
    public static RestMockHttpServletRequestBuilder PATCH(String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.patch(new URI(uri)).content(new HashMap<>());
    }
    public static RestMockHttpServletRequestBuilder DELETE(String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.delete(new URI(uri));
    }   
    public static RestMockHttpServletRequestBuilder MULTIPART(String uri) throws Exception{
        return RestMockHttpServletRequestBuilder.multipart(new URI(uri));
    }

   
    //////////////////////////////////////////////
    //
    //////////////////////////////////////////////
    public static ResultHandler print(){
        return (r)->{
            System.err.println("");
            System.err.println(r.getRequest().getRequestURL());
            System.err.println("--------------------------------------------");
            MockMvcResultHandlers.print(System.err).handle(r);
            System.err.println("--------------------------------------------");
            System.err.println("");
        };
    }

    public static ResultHandler json(BiConsumer<String,DocumentContext> consumer, String key){
        return (r)->{
            String body = r.getResponse().getContentAsString();
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(body);
            DocumentContext context = JsonPath.parse(document);
            consumer.accept(key, context);
        };
    }

    public static ResultHandler docs(Consumer<RestDocumentationResultHandlerBuilder> consumer, String identifier) {
        return RestDocumentationResultHandlerBuilder.build(identifier, consumer);
    }


    //////////////////////////////////////////////
    //
    //////////////////////////////////////////////
    public static ResultMatcher is2xx(){
        return MockMvcResultMatchers.status().is2xxSuccessful();
    }
    public static ResultMatcher is3xx(){
        return MockMvcResultMatchers.status().is3xxRedirection();
    }
    public static ResultMatcher is4xx(){
        return MockMvcResultMatchers.status().is4xxClientError();
    }
    public static ResultMatcher is5xx(){
        return MockMvcResultMatchers.status().is5xxServerError();
    }

    public static <T> ResultMatcher isJson(String key, T value){
        JsonPathResultMatchers json = MockMvcResultMatchers.jsonPath(key);
        if(value == null) return json.doesNotExist();
       return json.value(Matchers.equalTo(value));
    }
}