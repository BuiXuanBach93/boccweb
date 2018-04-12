//package jp.bo.bocc.service.impl;
//
//import jp.bo.bocc.system.config.AppConfig;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.WebApplicationContext;
//
//import javax.servlet.http.HttpSession;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * @author NguyenThuong on 3/23/2017.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = AppConfig.class)
//@WebAppConfiguration
//@Transactional
//public class AdminDetailServiceImplTest {
//
//    @Autowired
//    UserDetailsService userDetailsService;
//
//    private MockMvc mockMvc;
//
//    @Autowired
//    private FilterChainProxy springSecurityFilterChain;
//
//    @Autowired
//    private MockHttpServletRequest request;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    @Autowired
//    private WebApplicationContext webappContext;
//
//    @Before
//    public void init() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webappContext)
//                .addFilters(springSecurityFilterChain)
//                .build();
//    }
//
//    @Test
//    public void testLoadUserByUserName() throws Exception {
//
//        HttpSession session = mockMvc.perform(MockMvcRequestBuilders.post("/login")
//                .param("username", "admin")
//                .param("password", "admin"))
//                .andDo(MockMvcResultHandlers.print())
////                .andExpect(MockMvcResultMatchers.status().isMovedTemporarily())
//                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?error"))
//                .andReturn()
//                .getRequest()
//                .getSession();
//
//        request.setSession(session);
//
//        SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
//        SecurityContextHolder.setContext(securityContext);
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
//        assertEquals(userDetails.getPassword(), "$5uKS72xK2ArGDgb2CwjYnOzQcOmB7CPxK6fz2MGcDBM9vJ4rUql36");
//    }
//}
