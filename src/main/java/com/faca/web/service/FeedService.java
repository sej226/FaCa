package com.faca.web.service;

import com.faca.web.model.*;
import com.faca.web.repository.FeedRepository;
import com.faca.web.repository.UserRepository;
import com.faca.web.util.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import static com.faca.web.model.MessageStatus.*;

@Service
public class FeedService {

  @Autowired
  private FeedRepository feedRepository;

  @Autowired
  private ConnectionRepository connectionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SessionUser sessionUser;

  public List<Feed> getAllFeedListFromFacebook() {
    return getAllFeedList();
  }

  @Transactional
  public List<Feed> getUnregisteredFeedListFromFacebook() {
    User user = sessionUser.getCurrentUser();
    List<Feed> feedList = user.getFeeds();
    List<Feed> facebookFeedList = null;

    if (feedList.size() != 0) {
      Feed newestFeed = feedList.get(0);

      for (Feed compareFeed : feedList) {
        if (newestFeed.getUpDatedTime().compareTo(compareFeed.getUpDatedTime()) < 0)
          newestFeed = compareFeed;
      }
      facebookFeedList = this.getUnregisteredFeedListFromFacebook(newestFeed);
    } else {
      facebookFeedList = this.getAllFeedList();
    }

    saveFeedsToDatabase(user, facebookFeedList);

    return facebookFeedList;
  }

  private void saveFeedsToDatabase(User user, List<Feed> facebookFeedList) {
    for (Feed feed : facebookFeedList) {
      user.addFeed(feedRepository.save(feed));
    }
    userRepository.save(user);
  }

  private List<Feed> getUnregisteredFeedListFromFacebook(Feed newestFeed) {
    Facebook facebook = getFacebookApi();
    PagedList<Post> posts = facebook.feedOperations().getFeed();
    List<Feed> feedList = new ArrayList<>();
    PagingParameters pagingParameters = null;

    do {
      for (Post post : posts) {
        // 시간 비교!! (DB에 있는 게시물의 날짜가 새로 가져오는 게시물보다 작은 경우 가져옴)
        if (newestFeed.getUpDatedTime().compareTo(post.getUpdatedTime()) < 0) {
          Feed feed = Feed.builder()
              .name(post.getName())
              .caption(post.getCaption())
              .message(post.getMessage())
              .description(post.getDescription())
              .imageUrl(post.getPicture())
              .link(post.getLink())
              .upDatedTime(post.getUpdatedTime())
              .build();

          feedList.add(feed);
        } else {
          return feedList;
        }
      }
      pagingParameters = posts.getNextPage();
      posts = facebook.feedOperations().getFeed(pagingParameters);
    } while (posts.getNextPage() != null);

    return feedList;
  }

  public List<Feed> getAllFeedListFromDatabase() {
    Connection connection = getConnection();
    String providerUserId = connection.getKey().getProviderUserId();
    User user = userRepository.findByProviderUserID(providerUserId);
    List<Feed> feedList = sortFeedList(user.getFeeds());

    return feedList;
  }

  private List<Feed> sortFeedList(List<Feed> feedList) {
    feedList.sort((o1, o2) -> o2.getUpDatedTime().compareTo(o1.getUpDatedTime()));
    return feedList;
  }

  public ResponseEntity<ResponseMessage> getAllFeeds(String token) {
    User user = userRepository.findByToken(token);
    ResponseMessage message;

    if (user == null) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    message = new ResponseMessage(true, sortFeedList(user.getFeeds()));
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
  }

  private List<Feed> getAllFeedList() {
    Facebook facebook = getFacebookApi();
    PagedList<Post> posts = facebook.feedOperations().getFeed();
    List<Feed> feedList = new ArrayList<>();
    PagingParameters pagingParameters = null;

    do {
      for (Post post : posts) {
        Feed feed = Feed.builder()
            .name(post.getName())
            .caption(post.getCaption())
            .message(post.getMessage())
            .description(post.getDescription())
            .imageUrl(post.getPicture())
            .link(post.getLink())
            .upDatedTime(post.getUpdatedTime())
            .build();

        feedList.add(feed);
      }
      pagingParameters = posts.getNextPage();
      posts = facebook.feedOperations().getFeed(pagingParameters);
    } while (posts.getNextPage() != null);

    return sortFeedList((feedList));
  }

  private Connection<Facebook> getConnection() {
    return connectionRepository.findPrimaryConnection(Facebook.class);
  }

  private Facebook getFacebookApi() {
    Connection<Facebook> connection = getConnection();
    Facebook facebook = connection != null ? connection.getApi() : null;

    return facebook;
  }

  public ResponseEntity<ResponseMessage> addCategoryToFeed(FeedCategoryDTO feedCategory, String token) {
    User user = userRepository.findByToken(token);
    ResponseMessage message;

    if (user == null) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    for (long feedNo : feedCategory.getFeedId()) {
      Feed feed = feedRepository.findOne(feedNo);
      if (!user.getFeeds().contains(feed)) {
        message = new ResponseMessage(false, INVALID_TOKEN);
        return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
      }

      Category category = findCategory(user.getCategories(), feedCategory.getCategory().getName());
      if (category == null) {
        message = new ResponseMessage(false, NOT_FOUND_CATEGORY);
        return new ResponseEntity<ResponseMessage>(message, HttpStatus.FORBIDDEN);
      }

      feed.setCategory(category);
      feedRepository.save(feed);
    }

    message = new ResponseMessage(true, SUCCESS_ADD_CATEGORY_TO_FEED);
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
  }

  public ResponseEntity<ResponseMessage> addCategoryToFeed(Long feedNo, String categoryName, String token) {
    User user = userRepository.findByToken(token);
    Feed feed = feedRepository.findOne(feedNo);
    ResponseMessage message;

    if (user == null || !user.getFeeds().contains(feed)) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    Category category = findCategory(user.getCategories(), categoryName);

    if (category == null) {
      message = new ResponseMessage(false, NOT_FOUND_CATEGORY);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.FORBIDDEN);
    }

    feed.setCategory(category);
    feedRepository.save(feed);

    message = new ResponseMessage(true, SUCCESS_ADD_CATEGORY_TO_FEED);
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
  }

  public ResponseEntity<ResponseMessage> getCategoryFeed(String categoryName, String token) {
    User user = userRepository.findByToken(token);
    List<Feed> userFeedList = user.getFeeds();
    List<Feed> feedList = getCategoryFeed(userFeedList, categoryName);
    ResponseMessage message;

    if (user == null) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    message = new ResponseMessage(true, feedList);
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
  }

  private List<Feed> getCategoryFeed(List<Feed> userFeedList, String categoryName) {
    List<Feed> feedList = new ArrayList<>();

    for (Feed feed : userFeedList) {
      Category category = feed.getCategory();

      if (category != null) {
        if (category.equals(categoryName))
          feedList.add(feed);
      }
    }
    return sortFeedList(feedList);
  }

  private Category findCategory(List<Category> categories, String categoryName) {
    for (Category category : categories) {
      if (category.getName().equals(categoryName))
        return category;
    }
    return null;
  }

  public ResponseEntity<ResponseMessage> deleteCategoryFeed(Long feedNo, String token) {
    User user = userRepository.findByToken(token);
    List<Feed> userFeedList = user.getFeeds();
    ResponseMessage message;
    Feed feed = getFeed(userFeedList, feedNo);

    if (user == null || feed == null) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    feed.setCategory(null);
    feedRepository.save(feed);

    message = new ResponseMessage(true, SUCCESS_DELETE_CATEGORY_TO_FEED);
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
  }

  private Feed getFeed(List<Feed> userFeedList, Long feedNo) {
    Feed feed = null;
    for (Feed f : userFeedList) {
      if (f.getId() == feedNo) {
        feed = f;
        break;
      }
    }
    return feed;
  }

  public ResponseEntity<ResponseMessage> getUncategorizedFeeds(String token) {
    User user = userRepository.findByToken(token);
    ResponseMessage message;

    if (user == null) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    List<Feed> uncategorizedFeeds = sortFeedList(getUncategorizedFeeds(user.getFeeds()));
    message = new ResponseMessage(true, uncategorizedFeeds);
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
  }

  private List<Feed> getUncategorizedFeeds(List<Feed> userFeeds) {
    List<Feed> feeds = new ArrayList<>();

    for (Feed feed : userFeeds) {
      if (feed.getCategory() == null)
        feeds.add(feed);
    }
    return sortFeedList(feeds);
  }
}
