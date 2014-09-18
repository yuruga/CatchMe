package jp.yuruga.catchme.event;

import java.util.EventListener;

/**
 * Created by ichikawa on 2014/09/17.
 */
public interface RoomEventListenerInterface extends EventListener {


    /**
     * ルームに接続された時通知
     */
    public void onConnect(Object obj);

    /**
     * ルームから切断した時通知
     */
    public void onDisconnect();

    /**
     * ルームプロパティが更新された時通知
     */
    public void onChangeRoomProp(Object obj);

    /**
     * ユーザプロパティが更新された時通知
     */
    public void onChangeUserProp(Object[] arr);

    /**
     * メッセージが配信された時通知
     */
    public void onReceiveMessage(String message);
}
