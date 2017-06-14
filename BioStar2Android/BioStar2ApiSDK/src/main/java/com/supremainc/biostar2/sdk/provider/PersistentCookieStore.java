package com.supremainc.biostar2.sdk.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

/*
 * Copyright (c) 2015 Fran Montiel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// modified by Suprema 2015-09
public class PersistentCookieStore implements CookieStore {
    private static final String TAG = PersistentCookieStore.class.getSimpleName();

    // Persistence
    private static final String SP_COOKIE_STORE = "cookieStore";
    private static final String SP_KEY_DELIMITER = "|"; // Unusual char in URL
    private static final String SP_KEY_DELIMITER_REGEX = "\\" + SP_KEY_DELIMITER;
    private SharedPreferences sharedPreferences;
    // In memory
    private Map<URI, Set<HttpCookie>> allCookies;

    public PersistentCookieStore(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_COOKIE_STORE, Context.MODE_PRIVATE);
        createNewKey(context);
        loadAllFromPersistence();
    }

    /**
     * Get the real URI from the cookie "domain" and "path" attributes, if they
     * are not set then uses the URI provided (coming from the response)
     *
     * @param uri
     * @param cookie
     * @return
     */
    private static URI cookieUri(URI uri, HttpCookie cookie) {
        URI cookieUri = uri;
        if (cookie.getDomain() != null) {
            // Remove the starting dot character of the domain, if exists (e.g:
            // .domain.com -> domain.com)
            String domain = cookie.getDomain();
            if (domain.charAt(0) == '.') {
                domain = domain.substring(1);
            }
            try {
                cookieUri = new URI(uri.getScheme() == null ? "http" : uri.getScheme(), domain, cookie.getPath() == null ? "/" : cookie.getPath(), null);
            } catch (URISyntaxException e) {
                if (ConfigDataProvider.DEBUG) {
                    Log.w(TAG, e);
                }
            }
        }
        return cookieUri;
    }

    public boolean isValid() {
        if (allCookies != null) {
            if (allCookies.size() < 1) {
                return false;
            }
            for (URI key : allCookies.keySet()) {
                Set<HttpCookie> cookie = allCookies.get(key);
                Iterator<HttpCookie> keys = cookie.iterator();
                while (keys.hasNext()) {
                    HttpCookie data = keys.next();
                    String t = data.getName();
                    if (t != null && t.indexOf("-cloud-sessi") > -1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void loadAllFromPersistence() {
        allCookies = new HashMap<URI, Set<HttpCookie>>();

        Map<String, ?> allPairs = sharedPreferences.getAll();
        for (Entry<String, ?> entry : allPairs.entrySet()) {
            String[] uriAndName = entry.getKey().split(SP_KEY_DELIMITER_REGEX, 2);
            try {
                URI uri = new URI(uriAndName[0]);
                String encodedCookie = (String) entry.getValue();
                HttpCookie cookie = new SerializableHttpCookie().decode(encodedCookie);
                String value = decrypt(cookie.getValue());
                if (value != null && !value.isEmpty()) {
                    cookie.setValue(value);
                }
                Set<HttpCookie> targetCookies = allCookies.get(uri);
                if (targetCookies == null) {
                    targetCookies = new HashSet<HttpCookie>();
                    allCookies.put(uri, targetCookies);
                }
                // Repeated cookies cannot exist in persistence
                // targetCookies.remove(cookie)
                targetCookies.add(cookie);
            } catch (URISyntaxException e) {
                if (ConfigDataProvider.DEBUG) {
                    Log.w(TAG, "e:" + e.getMessage());
                }
            }
        }
    }

    @Override
    public synchronized void add(URI uri, HttpCookie cookie) {
        uri = cookieUri(uri, cookie);

        Set<HttpCookie> targetCookies = allCookies.get(uri);
        if (targetCookies == null) {
            targetCookies = new HashSet<HttpCookie>();
            allCookies.put(uri, targetCookies);
        }
        targetCookies.remove(cookie);
        targetCookies.add(cookie);

        saveToPersistence(uri, cookie);
    }

    private boolean createNewKey(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            KeyStore keyStore = null;
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);

                String alias = "master";
                if (!keyStore.containsAlias(alias)) {
                    Calendar start = Calendar.getInstance();
                    start.set(Calendar.YEAR, 2000);
                    Calendar end = Calendar.getInstance();
                    end.set(Calendar.YEAR, 3000);
                    KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(alias)
                            //                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                            .setSubject(new X500Principal("CN=" + alias))
                            .setSerialNumber(BigInteger.TEN)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();

                    KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                    generator.initialize(spec);
                    KeyPair keyPair = generator.generateKeyPair();
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private String encrypt(String data) {
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("master", null);
                Cipher inputCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
                byte[] encdoed = inputCipher.doFinal(data.getBytes("UTF-8"));
                return Base64.encodeToString(encdoed, Base64.DEFAULT);
            } catch (Exception e) {
                if (ConfigDataProvider.DEBUG) {
                    Log.e(TAG, "e:" + e.getMessage());
                }
            }
        }
        return null;
    }

    private String decrypt(String data) {
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("master", null);
                Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
                byte[] result = output.doFinal(Base64.decode(data, Base64.DEFAULT));
                return new String(result);
            } catch (Exception e) {
                if (ConfigDataProvider.DEBUG) {
                    Log.e(TAG, "e:" + e.getMessage());
                }
            }
        }
        return null;
    }


    private void saveToPersistence(URI uri, HttpCookie cookie) {
        HttpCookie cookie2;
        try {
            cookie2 = (HttpCookie)cookie.clone();
            String value = encrypt(cookie.getValue());
            if (value != null && !value.isEmpty()) {
                cookie2.setValue(value);
            }
        } catch (Exception e) {
            cookie2 = cookie;
            if (ConfigDataProvider.DEBUG) {
                Log.e(TAG, "e:" + e.getMessage());
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(uri.toString() + SP_KEY_DELIMITER + cookie.getName(), new SerializableHttpCookie().encode(cookie2));
        editor.apply();
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri) {
        return getValidCookies(uri);
    }

    @Override
    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> allValidCookies = new ArrayList<HttpCookie>();
        for (Iterator<URI> it = allCookies.keySet().iterator(); it.hasNext(); ) {
            allValidCookies.addAll(getValidCookies(it.next()));
        }

        return allValidCookies;
    }

    private String parseAuthority(String authority) {
        String tempUserInfo = null;
        String temp = authority;
        int index = temp.indexOf('@');
        int hostIndex = 0;
        if (index != -1) {
            // remove user info
            tempUserInfo = temp.substring(0, index);
            temp = temp.substring(index + 1); // host[:port] is left
            hostIndex = index + 1;
        }

        index = temp.lastIndexOf(':');
        int endIndex = temp.indexOf(']');

        String tempHost;
        int tempPort = -1;
        if (index != -1 && endIndex < index) {
            // determine port and host
            tempHost = temp.substring(0, index);

            if (index < (temp.length() - 1)) { // port part is not empty
                try {
                    char firstPortChar = temp.charAt(index + 1);
                    if (firstPortChar >= '0' && firstPortChar <= '9') {
                        // allow only digits, no signs
                        tempPort = Integer.parseInt(temp.substring(index + 1));
                    } else {
                        return null;
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        } else {
            tempHost = temp;
        }

        if (tempHost.isEmpty()) {
            return null;
        }
        //userInfo = tempUserInfo;
        return tempHost;
        //port = tempPort;
    }

    private List<HttpCookie> getValidCookies(URI uri) {
        Set<HttpCookie> targetCookies = new HashSet<HttpCookie>();
        // If the stored URI does not have a path then it must match any URI in
        // the same domain
        String host = uri.getHost();
        if (host == null) {
            host = parseAuthority(uri.getAuthority());
        }

        for (Iterator<URI> it = allCookies.keySet().iterator(); it.hasNext(); ) {
            URI storedUri = it.next();
            String storedHost = storedUri.getHost();
            String storedPath = null;
            if (storedHost == null) {
                storedHost = parseAuthority(storedUri.getAuthority());
                storedPath = "/";
            }

            // Check ith the domains match according to RFC 6265
//			if (checkDomainsMatch(storedUri.getHost(), uri.getHost())) {
            if (checkDomainsMatch(host, storedHost)) {
                // Check if the paths match according to RFC 6265
                if (storedPath == null) {
                    storedPath = storedUri.getPath();
                }
                if (checkPathsMatch(storedPath, uri.getPath())) {
                    targetCookies.addAll(allCookies.get(storedUri));
                }
            }
        }

        // Check it there are expired cookies and remove them
        if (targetCookies != null) {
            List<HttpCookie> cookiesToRemoveFromPersistence = new ArrayList<HttpCookie>();
            for (Iterator<HttpCookie> it = targetCookies.iterator(); it.hasNext(); ) {
                HttpCookie currentCookie = it.next();
                if (currentCookie.hasExpired()) {
                    cookiesToRemoveFromPersistence.add(currentCookie);
                    it.remove();
                }
            }

            if (!cookiesToRemoveFromPersistence.isEmpty()) {
                removeFromPersistence(uri, cookiesToRemoveFromPersistence);
            }
        }
        return new ArrayList<HttpCookie>(targetCookies);
    }

	/*
     * http://tools.ietf.org/html/rfc6265#section-5.1.3
	 * 
	 * A string domain-matches a given domain string if at least one of the
	 * following conditions hold:
	 * 
	 * o The domain string and the string are identical. (Note that both the
	 * domain string and the string will have been canonicalized to lower case
	 * at this point.)
	 * 
	 * o All of the following conditions hold:
	 * 
	 * The domain string is a suffix of the string.
	 * 
	 * The last character of the string that is not included in the domain
	 * string is a %x2E (".") character.
	 * 
	 * The string is a host name (i.e., not an IP address).
	 */

    private boolean checkDomainsMatch(String cookieHost, String requestHost) {
        if (cookieHost == null || requestHost == null) {
            return false;
        }
        return requestHost.equals(cookieHost) || requestHost.endsWith("." + cookieHost);
    }

	/*
	 * http://tools.ietf.org/html/rfc6265#section-5.1.4
	 * 
	 * A request-path path-matches a given cookie-path if at least one of the
	 * following conditions holds:
	 * 
	 * o The cookie-path and the request-path are identical.
	 * 
	 * o The cookie-path is a prefix of the request-path, and the last character
	 * of the cookie-path is %x2F ("/").
	 * 
	 * o The cookie-path is a prefix of the request-path, and the first
	 * character of the request-path that is not included in the cookie- path is
	 * a %x2F ("/") character.
	 */

    private boolean checkPathsMatch(String cookiePath, String requestPath) {
        return requestPath.equals(cookiePath) || (requestPath.startsWith(cookiePath) && cookiePath.charAt(cookiePath.length() - 1) == '/')
                || (requestPath.startsWith(cookiePath) && requestPath.substring(cookiePath.length() - 1).charAt(0) == '/');
    }

    private void removeFromPersistence(URI uri, List<HttpCookie> cookiesToRemove) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (HttpCookie cookieToRemove : cookiesToRemove) {
            editor.remove(uri.toString() + SP_KEY_DELIMITER + cookieToRemove.getName());
        }
        editor.apply();
    }

    @Override
    public synchronized List<URI> getURIs() {
        return new ArrayList<URI>(allCookies.keySet());
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie cookie) {
        Set<HttpCookie> targetCookies = allCookies.get(uri);
        boolean cookieRemoved = targetCookies != null ? targetCookies.remove(cookie) : false;
        if (cookieRemoved) {
            removeFromPersistence(uri, cookie);
        }
        return cookieRemoved;

    }

    private void removeFromPersistence(URI uri, HttpCookie cookieToRemove) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(uri.toString() + SP_KEY_DELIMITER + cookieToRemove.getName());
        editor.apply();
    }

    @Override
    public synchronized boolean removeAll() {
        allCookies.clear();
        removeAllFromPersistence();
        return true;
    }

    private void removeAllFromPersistence() {
        sharedPreferences.edit().clear().apply();
    }
}