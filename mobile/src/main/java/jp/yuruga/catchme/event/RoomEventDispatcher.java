package jp.yuruga.catchme.event;

import java.util.Vector;

/**
 * Created by ichikawa on 2014/09/17.
 */
public class RoomEventDispatcher {

    private static RoomEventDispatcher _instance = null;
    private static Vector<RoomEventListenerInterface> _listener = new Vector<RoomEventListenerInterface>();

    /**
     * インスタンス取得
     * @return RoomEventDispatcher
     */
    public static RoomEventDispatcher getInstance() {
        if(_instance == null){
            _instance = new RoomEventDispatcher();
        }
        return _instance;
    }

    /**
     * コンストラクタ
     */
    private RoomEventDispatcher() {
        //TODO: socketアダプタ
    }

    /**
     * リスナー追加
     * @param listener RoomEventListenerInterface
     */
    public static void addEventListener(RoomEventListenerInterface listener) {
        _listener.add(listener);
    }

    /**
     * リスナー削除
     * @param listener RoomEventListenerInterface
     */
    public static void removeEventListener(RoomEventListenerInterface listener) {
        if(_listener.contains(listener)){
            _listener.remove(listener);
        }
    }

    /**
     * 接続通知
     */
    public static void dispatchConnect(Object obj) {
        for(RoomEventListenerInterface listener : _listener){
            if(listener != null){
                listener.onConnect(obj);
            }
        }
    }

    /**
     * 切断通知
     */
    public static void dispatchDisconnect() {
        for(RoomEventListenerInterface listener : _listener){
            if(listener != null){
                listener.onDisconnect();
            }
        }
    }

    /**
     * ルームプロパティ更新通知
     */
    public static void dispatchChangeRoomProp(Object obj) {
        for(RoomEventListenerInterface listener : _listener){
            if(listener != null){
                listener.onChangeRoomProp(obj);
            }
        }
    }


    /**
     * ユーザプロパティ更新通知
     */
    public static void dispatchChangeUserProp(Object[] arr) {
        for(RoomEventListenerInterface listener : _listener){
            if(listener != null){
                listener.onChangeUserProp(arr);
            }
        }
    }

    /**
     * メッセージ通知
     */
    public static void dispatchReceiveMessage(String message) {
        for(RoomEventListenerInterface listener : _listener){
            if(listener != null){
                listener.onReceiveMessage(message);
            }
        }
    }
}
