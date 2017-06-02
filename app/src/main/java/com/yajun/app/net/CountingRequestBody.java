package com.yajun.app.net;

import android.os.RecoverySystem;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by yajun on 2017/2/17.
 *
 */
public class CountingRequestBody extends RequestBody {

    private RequestBody delegate;
    private RequestProgressListener mListener;
    private CountingSink countingSink;

    public CountingRequestBody(RequestBody delegate,RequestProgressListener mListener){
        this.delegate = delegate;
        this.mListener = mListener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength(){
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }


    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        countingSink = new CountingSink(sink);
        BufferedSink buffer = Okio.buffer(countingSink);
        delegate.writeTo(buffer);
        buffer.flush();
    }

    public class CountingSink extends ForwardingSink {

        private long bytesWritten;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            mListener.onRequestProgressListener(bytesWritten,delegate.contentLength());
        }
    }

    public static interface RequestProgressListener{
        void onRequestProgressListener(long byteWrited, long contentLength);
    }

}
