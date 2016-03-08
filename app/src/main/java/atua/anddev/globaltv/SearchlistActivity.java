package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import atua.anddev.globaltv.*;
import atua.anddev.globaltv.service.*;

import java.io.*;
import java.util.*;

public class SearchlistActivity extends Activity {
    private ChannelService channelService = MainActivity.channelService;
    private FavoriteService favoriteService = MainActivity.favoriteService;
    private PlaylistService playlistService = MainActivity.playlistService;
    private int selectedProvider = MainActivity.selectedProvider;
    private String searchString = MainActivity.searchString;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.searchlist);
        showSearchResults();

    }

    public void showSearchResults() {
        final ArrayList<String> playlist = new ArrayList<String>();
        final ArrayList<String> playlistUrl = new ArrayList<String>();
        String chName;
        for (int i = 0; i < channelService.sizeOfChannelList(); i++) {
            chName = channelService.getChannelById(i).getName().toLowerCase();
            if (chName.contains(searchString.toLowerCase())) {
                playlist.add(channelService.getChannelById(i).getName());
                playlistUrl.add(channelService.getChannelById(i).getUrl());
            }
        }

        TextView textView = (TextView) findViewById(R.id.searchlistTextView1);
        textView.setText(getResources().getString(R.string.resultsfor) + " '" + searchString + "' - " + playlist.size() + " " + getResources().getString(R.string.channels));

        ListView listView = (ListView) findViewById(R.id.searchlistListView1);
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(SearchlistActivity.this, s, Toast.LENGTH_SHORT).show();
                channelService.openURL(playlistUrl.get(p3), SearchlistActivity.this);
            }

        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(SearchlistActivity.this, s, Toast.LENGTH_SHORT).show();
                if (!favoriteService.containsNameForFavorite(s)) {
                    // Add to favourite list dialog
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SearchlistActivity.this);
                            builder.setTitle(getResources().getString(R.string.request));
                            builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.add) + " '" + s + "' to " + getResources().getString(R.string.tofavorites));
                            builder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    favoriteService.addToFavoriteList(s, playlistService.getActivePlaylistById(selectedProvider).getName());
                                    try {
                                        favoriteService.saveFavorites(SearchlistActivity.this);
                                    } catch (IOException e) {
                                    }
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    // nothing to do
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.setOwnerActivity(SearchlistActivity.this);
                            alert.show();
                        }
                    });
                }

                return true;
            }
        });
    }

}
