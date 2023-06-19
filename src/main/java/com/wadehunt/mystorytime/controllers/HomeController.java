package com.wadehunt.mystorytime.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.wadehunt.mystorytime.models.LoginUser;
import com.wadehunt.mystorytime.models.Story;
import com.wadehunt.mystorytime.models.User;
import com.wadehunt.mystorytime.services.JokeService;
import com.wadehunt.mystorytime.services.LikeService;
import com.wadehunt.mystorytime.services.ProfilePictureService;
import com.wadehunt.mystorytime.services.StoryService;
import com.wadehunt.mystorytime.services.StorySortingService;
import com.wadehunt.mystorytime.services.UserService;

@Controller
public class HomeController {
	
	@Autowired
	private UserService userServ;
	
	@Autowired
	private StoryService storyServ;
	
	@Autowired
	private ProfilePictureService profilePicServ;
	
	@Autowired
	private StorySortingService sortingServ;
	
	@Autowired
	private JokeService jokeServ;
	
	@Autowired
	private LikeService likeServ;

	@GetMapping("/")
	public String index(
			Model model,
			HttpSession session) {
		session.invalidate();
		model.addAttribute("loginUser", new LoginUser());
		model.addAttribute("joke", jokeServ.getJoke());
		return "index.jsp";
	}
	
	@GetMapping("/register")
	public String registerUser(
			Model model,
			HttpSession session) {
		session.invalidate();
		model.addAttribute("newUser", new User());
		return "register.jsp";
	}
	
	@PostMapping("/register")
	public String registerUser(
			@Valid
			@ModelAttribute("newUser") User newUser,
			BindingResult result,
			Model model,
			HttpSession session) {
			User user = userServ.register(newUser, result);
			if(result.hasErrors()) {
				model.addAttribute("loginUser", new LoginUser());
				return "register.jsp";
			}
			else {
				session.setAttribute("userId", user.getId());
				return "redirect:/dashboard/1";
			}		
	}
	
	@PostMapping("/login")
	public String loginUser(
			@Valid
			@ModelAttribute("loginUser") LoginUser loginUser,
			BindingResult result,
			Model model,
			HttpSession session) throws Exception {
			User user = userServ.loginUser(loginUser, result);
			if(result.hasErrors()) {
				model.addAttribute("newUser", new User());
				return "index.jsp";
			}
			else {
				session.setAttribute("userId", user.getId());
				return "redirect:/dashboard/1";
			}
	}
	
	@GetMapping("/dashboard/{pageNumber}")
	public String dashboard(
			@PathVariable("pageNumber") int pageNumber,
			Model model,
			HttpSession session,
			@ModelAttribute("loginUser") LoginUser loginUser,
			@ModelAttribute("newUser") User newUser,
			@ModelAttribute("date") Date date) {
		if(session.getAttribute("userId") == null) {
			return "redirect:/";
		}
		else {
			Page<Story> sortStories = sortingServ.storiesPerPage(pageNumber - 1);
			int totalPages = sortStories.getTotalPages();
			int currentPage = pageNumber;
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("pictureUrl", "/profilepicture/story/${story.storyAuthor.id}");
			model.addAttribute("user", userServ.findById((Long)session.getAttribute("userId")));
			model.addAttribute("pic", profilePicServ.findAll());
			model.addAttribute("stories", sortStories);
			return "dashboard.jsp";
		}
	}

	@GetMapping("/view/author/{userId}/{pageNumber}")
	public String viewMyStories(
			@PathVariable("userId") Long userId,
			@PathVariable("pageNumber") int pageNumber,
			Model model,
			HttpSession session
			) {
		if(session.getId() == null) {
			return "redirect:/";
		}
		else {
			User currentUser = userServ.findById((Long)session.getAttribute("userId"));
			Page<Story> sortStories = sortingServ.authorStoriesPerPage(pageNumber - 1, (Long)session.getAttribute("userId"));
			int totalPages = sortStories.getTotalPages();
			int currentPage = pageNumber;
			List<Story> userStories = storyServ.findByStoryAuthor(currentUser);
			model.addAttribute("pictureUrl", "/profilepicture/story/${story.storyAuthor.id}");
			model.addAttribute("user", userServ.findById((Long)session.getAttribute("userId")));
			model.addAttribute("stories", sortStories );
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("currentPage", currentPage);
			if(userStories.isEmpty()) {
				userStories = null;
			}
			return "myStories.jsp";
		}
	}
	

	@GetMapping("/logout")
	public String logout(
			Model model,
			HttpSession session) {
			session.invalidate();
			return "redirect:/";
	}
}
