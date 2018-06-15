package com.faca.web.controller;

import com.faca.web.service.FeedService;
import com.faca.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class WebController {

  @Autowired
  private FeedService feedService;

  @Autowired
  private UserService userService;

  @RequestMapping()
  public ModelAndView home() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("index");
    modelAndView.addObject("user", userService.getCurrentUser());
    return modelAndView;
  }

  @RequestMapping("feeds")
  public ModelAndView getAllFeeds() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/feed");
    modelAndView.addObject("user", userService.getCurrentUser());
    modelAndView.addObject("feedList", feedService.getAllFeedListFromDatabase());
    return modelAndView;
  }

  @RequestMapping("login")
  public String viewLoginPage() {
    return "login";
  }
}
