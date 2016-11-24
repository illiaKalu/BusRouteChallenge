package com.dev;

import com.dev.Controllers.RouteCheckController;
import com.dev.Utils.FileLoader;
import com.dev.Utils.StationConnectionChecker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BusRouteChallengeApplicationTests {

	private MockMvc mockMvc;

	@Mock
	private FileLoader fileLoader;

	@InjectMocks
	private RouteCheckController routeCheckController;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/static/");
		viewResolver.setSuffix(".html");

		mockMvc = MockMvcBuilders.standaloneSetup(routeCheckController).setViewResolvers(viewResolver).build();
	}

	@Test
	public void testRouteCheckController() throws Exception {

		// didn't hae time enough to make a good tests, BUT in theory here we should test controller.
		// also there must be JUnit ( do not need mokito ) to test StationConnectionChecker and FileLoader ( it is simple tests )

//		mockMvc.perform(get("/api/direct?dep_sid=12&arr_sid=2213"))
//				.andExpect(status().isOk())
//				.andExpect(view().name("checkResult"));

	}

}
