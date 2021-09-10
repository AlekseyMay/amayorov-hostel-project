package com.amayorov.hostel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class StarterController {

		@RequestMapping(value = {"/", "/swagger-ui", "/swagger-ui/", "/swagger"})
		public void handleStart(HttpServletResponse response) throws IOException {
			response.sendRedirect("/swagger-ui.html");
		}
}
