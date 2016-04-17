package me.ziyuanliu.stressmeter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by ziyuanliu on 4/16/16.
 */
public class StressMeterFragment extends Fragment{
    private ImageAdapter imgAdp;
    private GridView grid;

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public int counter = 0;
        private int[] currGrid;

        public void changeImageSet(){
            counter+=1;
            counter%=3;

        }

        public ImageAdapter(Context c) {
            mContext = c;
            currGrid = PSM.getGridById(counter);
        }

        public int getCount() {
            currGrid = PSM.getGridById(counter+1);
            return currGrid.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }


        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                DisplayMetrics dm = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                double padding = 100;
                double width=dm.widthPixels;
                imageView.setLayoutParams(new GridView.LayoutParams((int)(width/4), (int)(width/4)));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(currGrid[position]);
            return imageView;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stressmeter, container, false);
        super.onCreate(savedInstanceState);

        grid = (GridView) view.findViewById(R.id.grid_view);
        imgAdp = new ImageAdapter(view.getContext());
        grid.setAdapter(imgAdp);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent i = new Intent(getContext(), ImageSubmissionActivity.class);
                i.putExtra("gridIndex", imgAdp.counter);
                i.putExtra("photoIndex", position);
                startActivityForResult(i, 0);
            }
        });

        return view;
    }

    public void moreImagesClicked(View view){
        imgAdp.changeImageSet();
        imgAdp.notifyDataSetChanged();
        grid.invalidateViews();
        grid.setAdapter(imgAdp);
    }

}
