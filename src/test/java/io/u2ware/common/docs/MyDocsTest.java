package io.u2ware.common.docs;

import org.springframework.test.web.servlet.MockMvc;
import static io.u2ware.common.docs.MockMvcRestDocs.*;


public class MyDocsTest {
    
	private MockMvc mvc;

    

    // @Test
    public void contextLoads() throws Exception{


        MyDocs d = new MyDocs();

        mvc
        .perform(
            // MyDocs.get("aaaaaaa")
            GET(d::uri, "key.href.links")
            // MyDocs.get("", "", d::uri)
            .content(d.newEntity2())
            // .content(d::newEntity2)
            // .content(d::newEntity3 , "")
            // .content(d::newEntity4 , "", "")
        ).andDo(
            docs(d::search, "key")
        ).andDo(
            json(d::putJson, "key")
        ).andExpect(
            is2xx()
        ).andReturn();
    }
}
