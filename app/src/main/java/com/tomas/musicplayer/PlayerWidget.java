package com.tomas.musicplayer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class PlayerWidget extends AppWidgetProvider {
    final Player player = Player.getPlayer();
    private final String pauseSymbol = "❚❚";
    private final String playSymbol = "▶";

    //AlarmManager alarmManager = Context.getSystemService(Context.ALARM_SERVICE);
    //to set specific time update

    final MediaPlayer mp = MpWrapper.createMp();
    Boolean playing = false;

    public static String ACTION_WIDGET_PLAY = "ActionReceiverPlay";
    public static String ACTION_WIDGET_GO_TO_ACTIVITY = "ActionReceiverGoToActivity";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.player_widget);
        String songName = player.getCurrentSong().getTitle();
        remoteViews.setTextViewText(R.id.textView, String.valueOf(songName));
        remoteViews.setTextViewText(R.id.playButton, String.valueOf(pauseSymbol));

        Intent active = new Intent(context, PlayerWidget.class);
        active.setAction(ACTION_WIDGET_PLAY);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.playButton, actionPendingIntent);

        active = new Intent(context, PlayActivity.class);
        active.setAction(ACTION_WIDGET_GO_TO_ACTIVITY);
        active.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actionPendingIntent = PendingIntent.getActivity(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.textView, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_WIDGET_PLAY)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.player_widget);
            String songName = player.getCurrentSong().getTitle();
            remoteViews.setTextViewText(R.id.textView, String.valueOf(songName));
            if (!mp.isPlaying() && player.getCurrentSong() != null){
                mp.start();
                remoteViews.setTextViewText(R.id.playButton, String.valueOf(pauseSymbol));
            }
            else {
                mp.pause();
                remoteViews.setTextViewText(R.id.playButton, String.valueOf(playSymbol));
            }

            //to update the view
            ComponentName componentName = new ComponentName(context, PlayerWidget.class);
            AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
        } else if (intent.getAction().equals(ACTION_WIDGET_GO_TO_ACTIVITY)) {

        }
    }

}

