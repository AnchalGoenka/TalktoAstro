package tta.destinigo.talktoastro.volley.gson;

/* Created by Sonia Gupta on 2/20/2019. **/

import tta.destinigo.talktoastro.util.LogUtils;

public abstract class OnGsonParseCompleteListener<T> {
        public abstract void onParseComplete(T data);
        public abstract void onParseFailure(T data);
        public void onParseFailure(Exception exception) {
                LogUtils.e("Exception ", exception);
        }
}
