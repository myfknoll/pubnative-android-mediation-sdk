// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.mediation.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlacementListAdapter extends BaseAdapter {

    private Context      context;
    private List<String> placements;

    public PlacementListAdapter(Context context, List<String> placements) {
        this.context = context;
        if (placements != null) {
            this.placements = placements;
        } else {
            this.placements = new ArrayList();
        }
    }

    public void addPlacement(String placement) {
        if (this.placements != null) {
            this.placements.add(placement);
            notifyDataSetChanged();
        }
    }

    private void removePlacement(String placement) {
        if (this.placements != null) {
            this.placements.remove(placement);
            notifyDataSetChanged();
        }
    }

    public List<String> getPlacements() {
        return this.placements;
    }

    @Override
    public int getCount() {
        return placements.size();
    }

    @Override
    public String getItem(int position) {
        return placements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.placement_list_cell, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.placementText = (TextView) convertView.findViewById(R.id.placement_id_text);
            viewHolder.removePlacementButton = (ImageButton) convertView.findViewById(R.id.remove_placement_button);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder != null) {
            final String placementId = getItem(position);
            viewHolder.placementText.setText(placementId);
            viewHolder.removePlacementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removePlacement(placementId);
                }
            });
        }

        return convertView;
    }

    private class ViewHolder {

        TextView    placementText;
        ImageButton removePlacementButton;
    }
}
