/**
 * This file was automatically generated by the Mule Cloud Connector Development Kit
 */

package org.mule.twitter;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
import org.mule.tools.cloudconnect.annotations.Parameter;
import org.mule.tools.cloudconnect.annotations.Property;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import twitter4j.GeoLocation;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.http.alternative.HttpClientImpl;

/**
 * A Connector for Twitter which uses twitter4j.
 */
@Connector(namespacePrefix = "twitter")
public class TwitterConnector implements Initialisable, MuleContextAware {
    protected transient Log logger = LogFactory.getLog(getClass());

    private Twitter twitter;
    
    @Property
    private String consumerKey;

    @Property
    private String consumerSecret;

    @Property(optional=true)
    private String accessToken;

    @Property(optional=true)
    private String accessTokenSecret;

    @Property(optional=true)
    private boolean useSSL;
    
    @Override
    public void initialise() throws InitialisationException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setUseSSL(useSSL);
        
        twitter = new TwitterFactory(cb.build()).getInstance();

        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        if (accessToken != null) {
            twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        }
    }

    /**
     * Returns tweets that match a specified query. 
     * <p>
     * This method calls http://search.twitter.com/search.json
     * 
     * @param query The search query.
     * @return
     * @throws TwitterException
     */
    @Operation
    public QueryResult search(String query) throws TwitterException {
        return twitter.search(new Query(query));
    }
    
    /**
     * Returns the 20 most recent statuses from non-protected users who have set a custom user icon. The public timeline is cached for 60 seconds and requesting it more often than that is unproductive and a waste of resources.
     * <br>This method calls http://api.twitter.com/1/statuses/public_timeline
     *
     * @return list of statuses of the Public Timeline
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/public_timeline">GET statuses/public_timeline | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getPublicTimeline() throws TwitterException {
        return twitter.getPublicTimeline();
    }


    /**
     * Returns the 20 most recent statuses, including retweets, posted by the authenticating user and that user's friends. This is the equivalent of /timeline/home on the Web.<br>
     * Usage note: This home_timeline call is identical to statuses/friends_timeline, except that home_timeline also contains retweets, while statuses/friends_timeline does not for backwards compatibility reasons. In a future version of the API, statuses/friends_timeline will be deprected and replaced by home_timeline.
     * <br>This method calls http://api.twitter.com/1/statuses/home_timeline
     *
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return list of the home Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/home_timeline">GET statuses/home_timeline | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getHomeTimeline(@Parameter(defaultValue="1", optional=true) int page, 
                                                @Parameter(defaultValue="100",optional=true) int count, 
                                                @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getHomeTimeline(getPaging(page, count, sinceId));
    }


    /**
     * Returns the 20 most recent statuses posted from the authenticating user. It's also possible to request another user's timeline via the id parameter.<br>
     * This is the equivalent of the Web / page for your own user, or the profile page for a third party.<br>
     * For backwards compatibility reasons, retweets are stripped out of the user_timeline when calling in XML or JSON (they appear with 'RT' in RSS and Atom). If you'd like them included, you can merge them in from statuses retweeted_by_me.<br>
     * <br>This method calls http://api.twitter.com/1/statuses/user_timeline.json
     *
     * @param screenName specifies the screen name of the user for whom to return the user_timeline
     * @return list of the user Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/user_timeline">GET statuses/user_timeline | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getUserTimelineByScreenName(String screenName, @Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getUserTimeline(screenName, getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent statuses posted from the authenticating user. It's also possible to request another user's timeline via the id parameter.<br>
     * This is the equivalent of the Web / page for your own user, or the profile page for a third party.<br>
     * For backwards compatibility reasons, retweets are stripped out of the user_timeline when calling in XML or JSON (they appear with 'RT' in RSS and Atom). If you'd like them included, you can merge them in from statuses retweeted_by_me.<br>
     * <br>This method calls http://api.twitter.com/1/statuses/user_timeline.json
     *
     * @param userId specifies the ID of the user for whom to return the user_timeline
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return list of the user Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/user_timeline">GET statuses/user_timeline | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getUserTimelineByUserId(long userId, @Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getUserTimeline(userId, getPaging(page, count, sinceId));
    }

    protected Paging getPaging(int page, int count, long sinceId) {
         Paging paging = new Paging(page, count);
         if (sinceId > 0) {
             paging.setSinceId(sinceId);
         }
         return paging;
    }

    /**
     * Returns the 20 most recent statuses posted from the authenticating user. It's also possible to request another user's timeline via the id parameter.<br>
     * This is the equivalent of the Web / page for your own user, or the profile page for a third party.<br>
     * For backwards compatibility reasons, retweets are stripped out of the user_timeline when calling in XML or JSON (they appear with 'RT' in RSS and Atom). If you'd like them included, you can merge them in from statuses retweeted_by_me.<br>
     * <br>This method calls http://api.twitter.com/1/statuses/user_timeline.json
     *
     * @param userId specifies the ID of the user for whom to return the user_timeline
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return list of the user Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/user_timeline">GET statuses/user_timeline | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getUserTimeline(@Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getUserTimeline(getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent mentions (status containing @username) for the authenticating user.
     * <br>This method calls http://api.twitter.com/1/statuses/mentions
     *
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/mentions">GET statuses/mentions | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getMentions(@Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getMentions(getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent retweets posted by the authenticating user.
     * <br>This method calls http://api.twitter.com/1/statuses/retweeted_by_me
     *
     * @return the 20 most recent retweets posted by the authenticating user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/retweeted_by_me">GET statuses/retweeted_by_me | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getRetweetedByMe(@Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetedByMe(getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent retweets posted by the authenticating user's friends.
     * <br>This method calls http://api.twitter.com/1/statuses/retweeted_to_me
     *
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return the 20 most recent retweets posted by the authenticating user's friends.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/retweeted_to_me">GET statuses/retweeted_to_me | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getRetweetedToMe(@Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetedToMe(getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent tweets of the authenticated user that have been retweeted by others.
     * <br>This method calls http://api.twitter.com/1/statuses/retweets_of_me
     *
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return the 20 most recent tweets of the authenticated user that have been retweeted by others.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/retweets_of_me">GET statuses/retweets_of_me | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<Status> getRetweetsOfMe(@Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetsOfMe(getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent retweets posted by users the specified user follows. This method is identical to statuses/retweeted_to_me except you can choose the user to view.
     * <br>This method has not been finalized and the interface is subject to change in incompatible ways.
     * <br>This method calls http://api.twitter.com/1/statuses/retweeted_to_user
     *
     * @param screenName the user to view
     * @param paging     controls pagination. Supports since_id, max_id, count and page parameters.
     * @return the 20 most recent retweets posted by the authenticating user's friends.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://groups.google.com/group/twitter-api-announce/msg/34909da7c399169e">#newtwitter and the API - Twitter API Announcements | Google Group</a>
     */
    @Operation
    public ResponseList<Status> getRetweetedToUserByScreenName(String screenName, @Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetedToUser(screenName, getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent retweets posted by users the specified user follows. This method is identical to statuses/retweeted_to_me except you can choose the user to view.
     * <br>This method has not been finalized and the interface is subject to change in incompatible ways.
     * <br>This method calls http://api.twitter.com/1/statuses/retweeted_to_user
     *
     * @param userId the user to view
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return the 20 most recent retweets posted by the authenticating user's friends.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://groups.google.com/group/twitter-api-announce/msg/34909da7c399169e">#newtwitter and the API - Twitter API Announcements | Google Group</a>
     */
    @Operation
    public ResponseList<Status> getRetweetedToUserByUserId(long userId, @Parameter(defaultValue="1", optional=true) int page, @Parameter(defaultValue="100",optional=true) int count, @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetedToUser(userId, getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent retweets posted by the specified user. This method is identical to statuses/retweeted_by_me except you can choose the user to view.
     * <br>This method has not been finalized and the interface is subject to change in incompatible ways.
     * <br>This method calls http://api.twitter.com/1/statuses/retweeted_by_user
     *
     * @param screenName the user to view
     * @param paging     controls pagination. Supports since_id, max_id, count and page parameters.
     * @return the 20 most recent retweets posted by the authenticating user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://groups.google.com/group/twitter-api-announce/msg/34909da7c399169e">#newtwitter and the API - Twitter API Announcements | Google Group</a>
     */
    @Operation
    public ResponseList<Status> getRetweetedByUserByScreenName(String screenName,
                                                               @Parameter(defaultValue = "1", optional = true) int page,
                                                               @Parameter(defaultValue = "100", optional = true) int count,
                                                               @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetedByUser(screenName, getPaging(page, count, sinceId));
    }

    /**
     * Returns the 20 most recent retweets posted by the specified user. This method is identical to statuses/retweeted_by_me except you can choose the user to view.
     * <br>This method has not been finalized and the interface is subject to change in incompatible ways.
     * <br>This method calls http://api.twitter.com/1/statuses/retweeted_by_user
     *
     * @param userId the user to view
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return the 20 most recent retweets posted by the authenticating user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://groups.google.com/group/twitter-api-announce/msg/34909da7c399169e">#newtwitter and the API - Twitter API Announcements | Google Group</a>
     */
    @Operation
    public ResponseList<Status> getRetweetedByUserByUserId(long userId,
                                                           @Parameter(defaultValue = "1", optional = true) int page,
                                                           @Parameter(defaultValue = "100", optional = true) int count,
                                                           @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetedByUser(userId, getPaging(page, count, sinceId));
    }

    /**
     * Returns a single status, specified by the id parameter below. The status's author will be returned inline.
     * <br>This method calls http://api.twitter.com/1/statuses/show
     *
     * @param id the numerical ID of the status you're trying to retrieve
     * @return a single status
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/show/:id">GET statuses/show/:id | dev.twitter.com</a>
     */
    @Operation
    public Status showStatus(long id) throws TwitterException {
        return twitter.showStatus(id);
    }

    /**
     * Updates the authenticating user's status. A status update with text identical to the authenticating user's text identical to the authenticating user's current status will be ignored to prevent duplicates.
     * <br>This method calls http://api.twitter.com/1/statuses/update
     *
     * @param status the text of your status update
     * @return the latest status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/post/statuses/update">POST statuses/update | dev.twitter.com</a>
     */
    @Operation
    public Status updateStatus(String status, 
                               @Parameter(optional=true, defaultValue="-1") long inReplyTo,
                               @Parameter(optional=true) GeoLocation geoLocation) throws TwitterException {
        StatusUpdate update = new StatusUpdate(status);
        if (inReplyTo > 0) {
           update.setInReplyToStatusId(inReplyTo);
        }
        if (geoLocation != null) {
            update.setLocation(geoLocation);
        }
        
        return twitter.updateStatus(status);
    }

    /**
     * Destroys the status specified by the required ID parameter.<br>
     * Usage note: The authenticating user must be the author of the specified status.
     * <br>This method calls http://api.twitter.com/1/statuses/destroy
     *
     * @param statusId The ID of the status to destroy.
     * @return the deleted status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/post/statuses/destroy/:id">POST statuses/destroy/:id | dev.twitter.com</a>
     */
    @Operation
    public Status destroyStatus(long statusId) throws TwitterException {
        return twitter.destroyStatus(statusId);
    }

    /**
     * Retweets a tweet. Returns the original tweet with retweet details embedded.
     * <br>This method calls http://api.twitter.com/1/statuses/retweet
     *
     * @param statusId The ID of the status to retweet.
     * @return the retweeted status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/post/statuses/retweet/:id">POST statuses/retweet/:id | dev.twitter.com</a>
     */
    @Operation
    public Status retweetStatus(long statusId) throws TwitterException {
        return twitter.retweetStatus(statusId);
    }

    /**
     * Returns up to 100 of the first retweets of a given tweet.
     * <br>This method calls http://api.twitter.com/1/statuses/retweets
     *
     * @param statusId The numerical ID of the tweet you want the retweets of.
     * @return the retweets of a given tweet
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/retweets/:id">Tweets Resources › statuses/retweets/:id</a>
     * @since Twitter4J 2.0.10
     */
    @Operation
    public ResponseList<Status> getRetweets(long statusId) throws TwitterException {
        return twitter.getRetweets(statusId);
    }

    /**
     * Show user objects of up to 100 members who retweeted the status.
     * <br>This method calls http://api.twitter.com/1/statuses/:id/retweeted_by
     *
     * @param statusId The ID of the status you want to get retweeters of
     * @param paging controls pagination. Supports count and page parameters.
     * @return the list of users who retweeted your status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/:id/retweeted_by">GET statuses/:id/retweeted_by | dev.twitter.com</a>
     */
    @Operation
    public ResponseList<User> getRetweetedBy(long statusId,
                                             @Parameter(defaultValue = "1", optional = true) int page,
                                             @Parameter(defaultValue = "100", optional = true) int count,
                                             @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetedBy(statusId, getPaging(page, count, sinceId));
    }

    /**
     * Show user ids of up to 100 users who retweeted the status represented by id
     * <br />This method calls http://api.twitter.com/1/statuses/:id/retweeted_by/ids.format
     *
     * @param statusId The ID of the status you want to get retweeters of
     * @return IDs of users who retweeted the stats
     * @param paging controls pagination. Supports count and page parameters.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://dev.twitter.com/doc/get/statuses/:id/retweeted_by/ids">GET statuses/:id/retweeted_by/ids | dev.twitter.com</a>
     */
    @Operation
    public IDs getRetweetedByIDs(long statusId,
                                 @Parameter(defaultValue = "1", optional = true) int page,
                                 @Parameter(defaultValue = "100", optional = true) int count,
                                 @Parameter(defaultValue="-1",optional=true) int sinceId) throws TwitterException {
        return twitter.getRetweetedByIDs(statusId, getPaging(page, count, sinceId));
    }
    

    /**
     * Set the OAuth verifier after it has been retrieved via requestAuthorization. The resulting access tokens
     * will be logged to the INFO level so the user can reuse them as part of the configuration in the future
     * if desired.
     * 
     * @param oauthVerifier The OAuth verifier code from Twitter.
     */
    @Operation
    public void setOauthVerifier(String oauthVerifier) throws TwitterException {
        AccessToken accessToken = twitter.getOAuthAccessToken(oauthVerifier);
        logger.info("Got OAuth access tokens. Access token:"  + accessToken.getToken() + " Access token secret:" + accessToken.getTokenSecret());
    }

    /**
     * Start the OAuth request authorization process. This will request a token from Yammer and return
     * a URL which the user can visit to authorize the connector for their account.
     * @return The user authorization URL.
     */
    @Operation
    public String requestAuthorization(@Parameter(optional=true) String callbackUrl) throws TwitterException {
        RequestToken token = twitter.getOAuthRequestToken();
        
        return token.getAuthorizationURL();
    }


    public Twitter getTwitterClient() {
        return twitter;
    }

    public boolean getUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    @Override
    public void setMuleContext(MuleContext context) {
        HttpClientImpl.setMuleContext(context);
    }

}
