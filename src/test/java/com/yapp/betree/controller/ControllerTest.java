package com.yapp.betree.controller;

import com.yapp.betree.config.TestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@MockBean(classes = {JpaMetamodelMappingContext.class})
@Import(value = TestConfig.class)
public class ControllerTest {

    /**
     * ControllerTest extends해서
     .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
     헤더에 추가 필요
     * @throws Exception
     */
//    @Test
//    void Test() throws Exception {
//        mockMvc.perform(get("/api/forest")
//                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
//                .andDo(print())
//                .andExpect(status().isCreated());
//    }
}
