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
        //未使用
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
     * プロパティ更新通知
     */
    public static void dispatchChangeProp() {
        for(RoomEventListenerInterface listener : _listener){
            if(listener != null){
                listener.onChangeProp();
            }
        }
    }

    /**
     * メッセージ通知
     */
    public static void dispatchReceiveMessage() {
        for(RoomEventListenerInterface listener : _listener){
            if(listener != null){
                listener.onReceiveMessage();
            }
        }
    }
}
