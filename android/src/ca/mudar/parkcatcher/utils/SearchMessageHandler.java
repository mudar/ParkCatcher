
package ca.mudar.parkcatcher.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class SearchMessageHandler extends Handler {

    private final WeakReference<OnMessageHandledListener> mListener;

    /**
     * Caller must implement this interface to receive the handler's
     * message
     */
    public interface OnMessageHandledListener {
        public void OnMessageHandled(Message msg);
    }

    public SearchMessageHandler(OnMessageHandledListener target) {
        mListener = new WeakReference<OnMessageHandledListener>(target);
    }

    @Override
    public void handleMessage(Message msg)
    {
        OnMessageHandledListener target = mListener.get();
        if (target != null) {
            target.OnMessageHandled(msg);
        }
    }
}
