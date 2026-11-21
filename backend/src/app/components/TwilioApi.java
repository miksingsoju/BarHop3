package app.components;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Retrofit interface for the Twilio Messaging API.
 * The URL comes from twilio.messages-url, so the same client works with the real
 * https://api.twilio.com/2010-04-01/Accounts/{accountSid}/Messages.json and with FakeTwilio
 * (http://localhost:9997/fake_twilio/send). Null fields are left out of the request.
 */
public interface TwilioApi {

	@FormUrlEncoded
	@POST
	Call<ResponseBody> sendMessage(@Url String url,
			@Field("To") String to,
			@Field("From") String from,
			@Field("MessagingServiceSid") String messagingServiceSid,
			@Field("Body") String body);
}
