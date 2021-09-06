package com.yunbao.live.bean;

import com.yunbao.common.utils.L;


/**
 * Created by cxf on 2018/8/4.
 */

//public class TiFilter extends ImgTexFilter {
//
//    private SrcPin<ImgTexFrame> mSrcPin;
//    private GLRender mGLRender;
//    private int mOutTexture = ImgTexFrame.NO_TEXTURE;
//    private SinkPin<ImgTexFrame> mTexSinkPin;
//    private final Object BUF_LOCK = new Object();
//    private TiSDKManager mTiSDKManager;
//
//    public TiFilter(TiSDKManager tiSDKManager, GLRender glRender) {
//        super(glRender);
//        mTiSDKManager = tiSDKManager;
//        mGLRender = glRender;
//
//        mTexSinkPin = new TiFancyTexSinPin();
//
//        mSrcPin = new SrcPin<>();
//    }
//
//    @Override
//    public SinkPin<ImgTexFrame> getSinkPin() {
//        return mTexSinkPin;
//    }
//
//    @Override
//    public SrcPin<ImgTexFrame> getSrcPin() {
//        return mSrcPin;
//    }
//
//    @Override
//    public int getSinkPinNum() {
//        return 2;
//    }
//
//    private class TiFancyTexSinPin extends SinkPin<ImgTexFrame> {
//        @Override
//        public void onFormatChanged(Object format) {
////            L.e("美颜onFormatChanged：");
//            ImgTexFormat fmt = (ImgTexFormat) format;
//
//            mSrcPin.onFormatChanged(fmt);
//        }
//
//        @Override
//        public void onFrameAvailable(ImgTexFrame frame) {
////            L.e("美颜onFrameAvailable："+frame.textureId);
//            if (mSrcPin.isConnected()) {
//
//                synchronized (BUF_LOCK) {
////                    mOutTexture = mTiSDKManager.renderTexture2D(frame.textureId, frame.format.width, frame.format.height,
////                            TiRotation.CLOCKWISE_ROTATION_180, true);
//                    mOutTexture = TiSDKManager.getInstance().renderTexture2D(
//                            frame.textureId,//2D纹理Id
//                            frame.format.width,//图像宽度
//                            frame.format.height,//图像高度
//                            TiRotation.CLOCKWISE_ROTATION_180,//TiRotation枚举，图像顺时针旋转的角度
//                            true//图像是否左右镜像
//                    );
//                }
//            }
//
//            ImgTexFrame outFrame = new ImgTexFrame(frame.format, mOutTexture, null, frame.pts);
//            mSrcPin.onFrameAvailable(outFrame);
//        }
//
//        @Override
//        public void onDisconnect(boolean recursive) {
////            L.e("美颜onDisconnect：");
//            if (recursive) {
//                mSrcPin.disconnect(true);
//                if (mOutTexture != ImgTexFrame.NO_TEXTURE) {
//                    mGLRender.getFboManager().unlock(mOutTexture);
//                    mOutTexture = ImgTexFrame.NO_TEXTURE;
//                }
//                mTiSDKManager.destroy();
//            }
//        }
//    }
//}
