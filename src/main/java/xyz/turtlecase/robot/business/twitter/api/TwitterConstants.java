package xyz.turtlecase.robot.business.twitter.api;

public interface TwitterConstants {
    String USER_FIELDS_PARAMETER_NAME = "user.fields";
    String USER_FIELDS = "name,created_at,url,description,id,username";
    String USER_FIELDS_SAMPLE = "id,username";
    String TWEET_FIELDS_PARAMETER_NAME = "tweet.fields";
    String TWEET_FIELDS = "created_at,id,author_id";
    String LIST_FIELDS_PARAMETER_NAME = "list.fields";
    String LIST_FIELDS = "description,private,owner_id,created_at";
    String PAGINATION_TOKEN_PARAMETER_NAME = "pagination_token";
    String MAX_RESULT_PARAMETER_NAME = "max_results";
    Integer MAX_RESULT = 1000;
}
