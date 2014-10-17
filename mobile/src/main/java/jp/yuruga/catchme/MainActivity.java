package jp.yuruga.catchme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStartGameClicked(View view)
    {
        startGame();
    }

    public void onStopGameClicked(View view)
    {
        stopGame();
    }

    public void onTestSyncButtonClicked(View view)
    {
        //dummy button 1
        Intent i = new Intent(this, GameListenerService.class);
        i.setAction(MainService.ACTION_TEST_ACTION);
        this.startService(i);
    }

    private void startGame()
    {
        Intent i = new Intent(this, MainService.class);
        i.setAction(MainService.ACTION_START_GAME);
        this.startService(i);
    }

    private void stopGame()
    {
        Intent i = new Intent(this, MainService.class);
        i.setAction(MainService.ACTION_STOP_GAME);
        this.startService(i);
    }
}
