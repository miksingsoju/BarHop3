package app.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import app.components.TwilioApi;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

/** Builds the Retrofit TwilioApi with HTTP Basic auth (account SID + auth token). */
@Configuration
public class RetrofitConfig {

	@Value("${twilio.base-url:https://api.twilio.com/}")
	private String baseUrl;

	@Value("${twilio.account-sid:}")
	private String accountSid;

	@Value("${twilio.auth-token:}")
	private String authToken;

	@Bean
	public TwilioApi twilioApi() {
		final String basicAuth = Credentials.basic(accountSid, authToken);
		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.addInterceptor(chain -> {
					Request withAuth = chain.request().newBuilder().header("Authorization", basicAuth).build();
					return chain.proceed(withAuth);
				})
				.build();
		return new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(client)
				.build()
				.create(TwilioApi.class);
	}
}
