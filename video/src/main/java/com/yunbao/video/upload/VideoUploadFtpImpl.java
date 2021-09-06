package com.yunbao.video.upload;

import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.yunbao.common.http.Data;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.video.event.VideoDeleteEvent;
import com.yunbao.video.http.VideoHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by cxf on 2018/12/21.
 * 上传视频，不使用云存储，使用自建存储，比如ftp服务器等
 *   "info": {
 *             "url": "http://132.232.122.151:8080/group1/upload",    视频上传地址（视频封面也用这个地址）
 *             "token": "8663d6f8edcf9717e21e2e63aeb779ee",         视频上传token
 *             //"url_other": "http://132.232.122.151:8080/group1/upload",    老包用的
 *             "image_url": "http://132.232.122.151:8080/group1/upload",      图片上传地址
 *             "image_token": "8663d6f8edcf9717e21e2e63aeb779ee",           图片上传token
 *             "video_path": "newrhlive/video/video/2020/10/15",                   视频存放路径（视频封面也用这个路径）
 *             //"image_path": "newrhlive/image//2020/10/15",                       老包用的
 *             "live_path": "newrhlive/image/live/2020/10/15",                          直播封面存放路径
 *             "user_path": "newrhlive/image/user/2020/10/15"                        用户头像存放路径
 *         }
 */

public class VideoUploadFtpImpl implements VideoUploadStrategy {

    private static final String TAG = "VideoUploadFtpImpl";
    private UploadTask<String> mTask;
    private String videoUrl;
    private String imageUrl;
    private String image_path;
    private String video_path;


    @Override
    public void upload(final VideoUploadBean videoUploadBean, final VideoUploadCallback callback) {
        if (videoUploadBean == null || callback == null) {
            return;
        }

        VideoHttpUtil.getUploadUrlInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String url=obj.getString("url");
                    String token=obj.getString("token");
                    String url_other=obj.getString("url_other");
                    image_path=obj.getString("image_path");
                    video_path=obj.getString("video_path");


                    startUpLoad(videoUploadBean,token, url,callback);
                }
            }
        });
    }


    private void startUpLoad(final VideoUploadBean videoUploadBean, final String token, final String url, final VideoUploadCallback callback) {
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .params("scene", "default")
                .params("auth_token", token)
                .params("output", "json")
                .params("path", video_path)
                .params("file", videoUploadBean.getVideoFile())
//                .params("file1", videoUploadBean.getImageFile())
                .converter(new StringConvert());
        String mTag=videoUploadBean.getVideoFile().getName();
        mTask = OkUpload.request(mTag, postRequest)
                .save()
                .register(new UploadListener<String>(mTag) {
                    @Override
                    public void onStart(Progress progress) {
                        L.e(TAG, "onStart------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        L.e(TAG, "onProgress------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onError(Progress progress) {
                        L.e(TAG, "onProgress------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onFinish(String s, Progress progress) {
                        L.e(TAG, "onFinish------progress----->" + progress.fraction * 100);
                        L.e(TAG, "onFinish------s----->" + s);
                        try{
                            JSONObject obj = JSON.parseObject(s);
                            videoUrl=obj.getString("src");
                            startUpLoadPic(videoUploadBean,token,url,callback);
                        }catch (Exception e){
                            L.e(TAG, "onFinish------s----->资源服错误");
                        }
                    }

                    @Override
                    public void onRemove(Progress progress) {
                        L.e(TAG, "onRemove------progress----->" + progress);
                        if (callback != null) {
                            callback.onFailure();
                        }
                    }
                });
        mTask.start();
    }

    private void startUpLoadPic(final VideoUploadBean videoUploadBean, String token,String url,final VideoUploadCallback callback) {

        if(!videoUploadBean.getImageFile().exists()){
            if (videoUploadBean != null) {
                videoUploadBean.setResultVideoUrl(videoUrl);
                videoUploadBean.setResultImageUrl("");
                if (callback != null) {
                    callback.onSuccess(videoUploadBean);
                }
            }
            return;
        }
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .params("scene", "default")
                .params("auth_token", token)
                .params("output", "json")
                .params("path", video_path)
                .params("file", videoUploadBean.getImageFile())
//                .params("file1", videoUploadBean.getImageFile())
                .converter(new StringConvert());
        String mTag=videoUploadBean.getImageFile().getName();
        mTask = OkUpload.request(mTag, postRequest)
                .save()
                .register(new UploadListener<String>(mTag) {
                    @Override
                    public void onStart(Progress progress) {
                        L.e(TAG, "onStart------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        L.e(TAG, "onProgress------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onError(Progress progress) {
                        L.e(TAG, "onProgress------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onFinish(String s, Progress progress) {
                        L.e(TAG, "onFinish------progress----->" + progress.fraction * 100);
                        L.e(TAG, "onFinish------s----->" + s);
                        try{
                            JSONObject obj = JSON.parseObject(s);
                            imageUrl=obj.getString("src");
                            if (videoUploadBean != null) {
                                videoUploadBean.setResultVideoUrl(videoUrl);
                                videoUploadBean.setResultImageUrl(imageUrl);
                                if (callback != null) {
                                    callback.onSuccess(videoUploadBean);
                                }
                            }
                        }catch (Exception e){
                            L.e(TAG, "onFinish------s----->资源服错误");
                        }

//                        if (!TextUtils.isEmpty(s)) {
//                            try {
//                                JsonBean bean = JSON.parseObject(s, JsonBean.class);
//                                if (bean != null) {
//                                    if (200 == bean.getRet()) {
//                                        Data data = bean.getData();
//                                        if (data != null) {
//                                            if (700 == data.getCode()) {
//                                                //token过期，重新登录
//                                                RouteUtil.forwardLoginInvalid(data.getMsg());
//                                                if (callback != null) {
//                                                    callback.onFailure();
//                                                }
//                                            } else {
//                                                String[] info = data.getInfo();
//                                                if (data.getCode() == 0 && info.length > 0) {
//                                                    JSONObject obj = JSON.parseObject(info[0]);
//                                                    if (videoUploadBean != null) {
//                                                        videoUploadBean.setResultVideoUrl(obj.getString("video"));
//                                                        videoUploadBean.setResultImageUrl(obj.getString("img"));
//                                                        if (callback != null) {
//                                                            callback.onSuccess(videoUploadBean);
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        } else {
//                                            L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
//                                            if (callback != null) {
//                                                callback.onFailure();
//                                            }
//                                        }
//                                    } else {
//                                        L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
//                                        if (callback != null) {
//                                            callback.onFailure();
//                                        }
//                                    }
//
//                                } else {
//                                    L.e("服务器返回值异常--->bean = null");
//                                    if (callback != null) {
//                                        callback.onFailure();
//                                    }
//                                }
//                            } catch (Exception e) {
//                                if (callback != null) {
//                                    callback.onFailure();
//                                }
//                            }
//                        }
                    }

                    @Override
                    public void onRemove(Progress progress) {
                        L.e(TAG, "onRemove------progress----->" + progress);
                        if (callback != null) {
                            callback.onFailure();
                        }
                    }
                });
        mTask.start();
    }

    @Override
    public void cancel() {
        if (mTask != null) {
            mTask.remove();
        }
    }
}
