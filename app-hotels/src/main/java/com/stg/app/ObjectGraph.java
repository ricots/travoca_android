package com.stg.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.util.SimpleArrayMap;
import android.telephony.TelephonyManager;

import com.socialtravelguide.api.EtbApi;
import com.socialtravelguide.api.EtbApiConfig;
import com.socialtravelguide.api.mock.ResultsMockClient;
import com.socialtravelguide.api.model.SearchRequest;
import com.stg.app.analytics.Facebook;
import com.stg.app.etbapi.CacheRequestInterceptor;
import com.stg.app.etbapi.CacheResponseInterceptor;
import com.stg.app.etbapi.RetrofitLogger;
import com.stg.app.etbapi.UserAgentInterceptor;
import com.stg.app.member.MemberAdapter;
import com.stg.app.member.MemberService;
import com.stg.app.member.MemberStorage;
import com.stg.app.model.HotelListRequest;
import com.stg.app.preferences.UserPreferences;
import com.stg.app.preferences.UserPreferencesStorage;
import com.stg.app.utils.DefaultHttpClient;
import com.stg.app.utils.NetworkUtilities;
import com.stg.app.utils.PriceRender;
import com.facebook.CallbackManager;
import com.facebook.device.yearclass.YearClass;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.concurrent.TimeUnit;

/**
 * @author ortal
 * @date 2015-04-07
 */
public class ObjectGraph {


    private static final long DISK_CACHE_SIZE = 5000000; // 5mbX
    private static final long CONNECT_TIMEOUT_MILLIS = 30000;
    private static final long READ_TIMEOUT_MILLIS = 40000;
    protected final Context app;
    private HotelListRequest mHotelsRequest;
    private Facebook mFacebook;
    private UserPreferences mUserPrefs;
    private EtbApi mEtbApi;
    private OkHttpClient mHttpClient;
    private SimpleArrayMap<String, PriceRender> mPriceRender;
    private MemberStorage mMemberStorage;
    private SearchRequest mLastSearchRequest;


    public ObjectGraph(Context applicationContext) {
        this.app = applicationContext;
    }

    public SearchRequest getLastSearchRequest() {
        return mLastSearchRequest;
    }

    public void updateLastSeatchRequest(SearchRequest request) {
        if (mLastSearchRequest == null) {
            mLastSearchRequest = new SearchRequest();
        }
        mLastSearchRequest.setType(request.getType());
        mLastSearchRequest.setNumberOfPersons(request.getNumberOfPersons());
        mLastSearchRequest.setNumbersOfRooms(request.getNumberOfRooms());
    }

    public HotelListRequest createHotelsRequest() {
        HotelListRequest request = new HotelListRequest();
        UserPreferences userPrefs = getUserPrefs();
        request.setLanguage(userPrefs.getLang());
        request.setCurrency(userPrefs.getCurrencyCode());
        request.setCustomerCountryCode(userPrefs.getCountryCode());
        return request;
    }

    public EtbApi etbApi() {
        if (mEtbApi == null) {
            EtbApiConfig cfg = new EtbApiConfig(Config.ETB_API_KEY, Config.ETB_API_CAMPAIGN_ID);
            cfg.setDebug(BuildConfig.DEBUG);
            cfg.setLogger(new RetrofitLogger());
            mEtbApi = new EtbApi(cfg, apiHttpClient());
        }
        return mEtbApi;
    }

    private OkHttpClient apiHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient(this.app);
            File directory = new File(this.app.getCacheDir(), "responses");

            mHttpClient.setCache(new Cache(directory, DISK_CACHE_SIZE));
            mHttpClient.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            mHttpClient.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

            mHttpClient.networkInterceptors().add(new CacheResponseInterceptor());
            mHttpClient.networkInterceptors().add(new UserAgentInterceptor(this.app));
            mHttpClient.interceptors().add(new CacheRequestInterceptor(new NetworkUtilities(connectivityManager())));
            mHttpClient.interceptors().add(RetrofitLogger.create());
            mHttpClient.interceptors().add(new ResultsMockClient());
        }
        return mHttpClient;
    }

    public UserPreferences getUserPrefs() {
        if (mUserPrefs == null) {
            UserPreferencesStorage storage = new UserPreferencesStorage(this.app);
            mUserPrefs = storage.load();
        }
        return mUserPrefs;
    }

    public TelephonyManager getTelephonyManager() {
        return (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public CallbackManager facebookCallbackManager() {
        return CallbackManager.Factory.create();
    }

    public Facebook facebook() {
        if (mFacebook == null) {
            mFacebook = new Facebook(app);
        }
        return mFacebook;
    }

    public ConnectivityManager connectivityManager() {
        return (ConnectivityManager) this.app.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public NetworkUtilities netUtils() {
        return new NetworkUtilities(connectivityManager());
    }


    public PriceRender priceRender(int numOfDays) {
        String currencyCode = getUserPrefs().getCurrencyCode();
        int priceShowType = getUserPrefs().getPriceShowType();
        if (mPriceRender == null || !mPriceRender.containsKey(currencyCode + "-" + numOfDays + "-" + priceShowType)) {
            mPriceRender = new SimpleArrayMap<>(1);
            PriceRender renderer = new PriceRender(priceShowType, getNumberFormatter(currencyCode), numOfDays);
            mPriceRender.put(currencyCode, renderer);
        }
        return mPriceRender.get(currencyCode);
    }

    public MemberStorage memberStorage() {
        if (mMemberStorage == null) {
            mMemberStorage = new MemberStorage(this.app);
        }
        return mMemberStorage;
    }

    public int getDeviceClass() {
        return YearClass.get(this.app);
    }

    public MemberService memberService() {
        DefaultHttpClient httpClient = new DefaultHttpClient(this.app);
        httpClient.setReadTimeout(3, TimeUnit.MINUTES);
        return (new MemberAdapter(memberStorage(), httpClient)).create();
    }

    public NumberFormat getNumberFormatter(String currencyCode) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
        if (formatter instanceof DecimalFormat) {
            formatter.setCurrency(Currency.getInstance(currencyCode));
        }
        return formatter;
    }

}