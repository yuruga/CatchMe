package jp.yuruga.catchme.event;

import java.util.EventListener;

/**
 * Created by ichikawa on 2014/09/17.
 */
public interface RoomEventListenerInterface extends EventListener {

    /**
     * ルームプロパティが更新された時通知
     */
    public void onChangeProp();

    /**
     * メッセージが配信された時通知
     */
    public void onReceiveMessage();
}
