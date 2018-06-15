package com.faca.web.controller;

import com.faca.web.model.Feed;
import com.faca.web.service.FeedService;
import com.faca.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
@RequestMapping("/feeds")
public class FeedController {

  @Autowired
  private FeedService feedService;

  @Autowired
  private UserService userService;

  @RequestMapping("/sync")
  public ModelAndView feedSync() {
    ModelAndView modelAndView = new ModelAndView();
    List<Feed> feedList = feedService.getUnregisteredFeedListFromFacebook();
    modelAndView.addObject("user", userService.getCurrentUser());
    modelAndView.addObject("feedList", feedList);
    modelAndView.setViewName("feed");

    return modelAndView;
  }

}
