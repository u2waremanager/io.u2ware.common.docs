package io.u2ware.common.docs;

import org.springframework.stereotype.Component;

@Component
public class MyDocs extends MockMvcRestDocs{

    public Object newEntity2(){
        return null;
    }

    public Object newEntity3(Object as){
        return null;
    }
    public Object newEntity4(Object as, Object b){
        return null;
    }

    public void search(RestDocumentationResultHandlerBuilder b) {

    }
}
