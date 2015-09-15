/*
 * Copyright 2015 Suprema(biostar2@suprema.co.kr)
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
package com.supremainc.biostar2.base;


import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.signature.StringSignature;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import java.io.IOException;
import java.io.InputStream;


public class GlidePhotoData {
	ImageView mImageView;
	String mIdentify;
	Resource<GifBitmapWrapper> mGifBitmapWrapperResource;
	Resource<Bitmap> mBitmapResource;
	GifBitmapWrapper mGifBitmapWrapper;
	Bitmap mBmp;
	String mUrl;
	int mMaxSize;

	public GlidePhotoData(Activity activity,ImageView view,int defaultResID,String lastModify,int maxsize, String subdomain, String url) {
		mImageView = view;
		mIdentify = subdomain+url;
		mUrl = url;
		mMaxSize = maxsize;
		Glide.with(activity)
				.load(url)
				.placeholder(defaultResID)
				.signature(new StringSignature(lastModify))
				.decoder(new ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper>() {
					@Override
					public Resource<GifBitmapWrapper> decode(ImageVideoWrapper imageVideoWrapper, int i, int i1) throws IOException {
						InputStream inputStream = imageVideoWrapper.getStream();
						StringBuffer sb = new StringBuffer();
						byte[] b = new byte[4096];
						boolean findData = false;
						for (int n; (n = inputStream.read(b)) != -1; ) {
							String data = new String(b, 0, n);
							sb.append(data);
							if (findData == false && sb.length() > "data:image/jpeg;base64,".length()) {
								findData = true;
								int index = sb.indexOf(",");
								sb = sb.delete(0,index+1);
							}
						}
						String result = sb.toString();
						Bitmap bmp = convertBitmap(result);
						if (bmp == null) {
							return null;
						}
						setPhotoData(bmp);
						return getGifBitmapWrapperResource();
					}

					@Override
					public String getId() {
						return mIdentify;
					}
				})
				.crossFade()
				.fitCenter()
				.into(mImageView);
	}

	public void setPhotoData(Bitmap bmp) {
		mBmp = bmp;
		mBitmapResource = new Resource<Bitmap>(){
			@Override
			public Bitmap get() {
				return mBmp;
			}

			@Override
			public int getSize() {
				return mBmp.getByteCount();
			}

			@Override
			public void recycle() {
				if (mBmp != null) {
					mBmp.recycle();
				}
				mBmp = null;
			}
		};
		mGifBitmapWrapper = new GifBitmapWrapper(mBitmapResource,null);
		mGifBitmapWrapperResource = new  Resource<GifBitmapWrapper>(){
			@Override
			public GifBitmapWrapper get() {
				return mGifBitmapWrapper;
			}

			@Override
			public int getSize() {
				return mGifBitmapWrapper.getSize();
			}

			@Override
			public void recycle() {
				mGifBitmapWrapper.getBitmapResource().recycle();
			}
		};
	}

	public Resource<Bitmap> getBitmapResource() {
		return mBitmapResource;
	}

	public Resource<GifBitmapWrapper> getGifBitmapWrapperResource() {
		return mGifBitmapWrapperResource;
	}

	private Bitmap convertBitmap(String data) {
		if (data == null) {
			return null;
		}
		try {
			byte[] photo = Base64.decode(data, 0);
			Bitmap bmpSource = ImageUtil.byteArrayToBitmap(photo);
			Bitmap bmp = ImageUtil.resizeBitmap(bmpSource, mMaxSize, true);
			if (bmp != null) {
				Bitmap rBmp = ImageUtil.getRoundedBitmap(bmp, true);
				return rBmp;
			}
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.e("convertBitmap"," e:"+e.getMessage());
			}
		}
		return null;
	}
}
