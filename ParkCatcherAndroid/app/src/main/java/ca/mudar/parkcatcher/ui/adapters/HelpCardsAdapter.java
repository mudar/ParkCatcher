/*
    Park Catcher Montréal
    Find a free parking in the nearest residential street when driving in
    Montréal. A Montréal Open Data project.

    Copyright (C) 2012 Mudar Noufal <mn@mudar.ca>

    This file is part of Park Catcher Montréal.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.mudar.parkcatcher.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.HelpCard;
import ca.mudar.parkcatcher.utils.RoundedTransformation;

public class HelpCardsAdapter extends RecyclerView.Adapter<HelpCardsAdapter.ViewHolder> {
    private static final String TAG = "HelpCardsAdapter";

    private final Context context;
    private final int layout;
    private final List<HelpCard> dataset;
    // Style
    private final int cardElevation;
    private final int cardRadius;
    private final int cardBgColor;
    private final int primaryBgColor;

    public HelpCardsAdapter(Context context, int layout, List<HelpCard> dataset) {
        this.context = context;
        this.layout = layout;
        this.dataset = dataset;
        // Style
        this.cardElevation = context.getResources().getDimensionPixelSize(R.dimen.help_card_elevation);
        this.cardRadius = context.getResources().getDimensionPixelSize(R.dimen.help_card_corner_radius);
        this.cardBgColor = context.getResources().getColor(R.color.help_card_bg);
        this.primaryBgColor = context.getResources().getColor(R.color.theme_bg_primary);
    }

    @Override
    public HelpCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HelpCard card = dataset.get(position);

        if (holder.imageViewContainer != null) {
            holder.imageViewContainer.setVisibility(card.hasImage() ? View.VISIBLE : View.GONE);
        }
        if (holder.textView != null) {
            holder.textView.setVisibility(card.hasText() && card.hasImage() ? View.VISIBLE : View.GONE);
        }
        if (holder.wideTextView != null) {
            holder.wideTextView.setVisibility(card.hasText() && !card.hasImage() ? View.VISIBLE : View.GONE);
        }

        if (card.hasImage() && card.hasText()) {
            holder.cardView.setCardElevation(cardElevation);
            holder.cardView.setRadius(cardRadius);
            holder.cardView.setCardBackgroundColor(cardBgColor);
        } else {
            holder.cardView.setCardElevation(0);
            holder.cardView.setRadius(0);
            holder.cardView.setCardBackgroundColor(primaryBgColor);
        }

        if (card.hasImage() && holder.imageView != null) {
            // Show the image
            final RequestCreator picasso = Picasso.with(context).load(card.getImage());
            final int placeholder = card.getImagePlaceholder();
            if (placeholder != 0) {
                picasso.placeholder(placeholder)
                        .error(placeholder);
            }
            picasso.transform(new RoundedTransformation(cardRadius, 0))
                    .into(holder.imageView);

            // Set background only if text is available
            holder.imageViewContainer.setBackgroundResource(card.getImageBackground());
        }

        if (card.hasText()) {
            if (card.hasImage()) {
                // Show as regular text
                holder.textView.setText(card.getText());
            } else {
                // Show as wide flat text
                holder.wideTextView.setText(card.getText());
                if (card.hasTopDrawable()) {
                    holder.wideTextView.setCompoundDrawablesWithIntrinsicBounds(0, card.getTopDrawable(), 0, 0);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (dataset == null) ? 0 : dataset.size();
    }

    /**
     * The ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView textView;
        public TextView wideTextView;
        public ImageView imageView;
        public View imageViewContainer;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            textView = (TextView) itemView.findViewById(R.id.help_item_text);
            wideTextView = (TextView) itemView.findViewById(R.id.help_item_text_wide);
            imageView = (ImageView) itemView.findViewById(R.id.help_item_image);
            imageViewContainer = itemView.findViewById(R.id.help_item_image_container);
        }
    }
}