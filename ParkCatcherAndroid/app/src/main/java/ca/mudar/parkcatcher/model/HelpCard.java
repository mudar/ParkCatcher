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

package ca.mudar.parkcatcher.model;

import ca.mudar.parkcatcher.R;

public class HelpCard {
    private int image;
    private int text;
    private int topDrawable;
    private int imageBackground = R.drawable.bg_card_radius;
    private int imagePlaceholder = R.drawable.help_panel_placeholder;

    public HelpCard(int image, int text) {
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public int getImageBackground() {
        return imageBackground;
    }

    public HelpCard setImageBackground(int imageBackground) {
        this.imageBackground = imageBackground;
        return this;
    }

    public int getImagePlaceholder() {
        return imagePlaceholder;
    }

    public HelpCard setImagePlaceholder(int imagePlaceholder) {
        this.imagePlaceholder = imagePlaceholder;
        return this;
    }

    public int getTopDrawable() {
        return topDrawable;
    }

    public HelpCard setTopDrawable(int drawable) {
        this.topDrawable = drawable;
        return this;
    }

    public int getText() {
        return text;
    }

    public boolean hasImage() {
        return image != 0;
    }

    public boolean hasText() {
        return text != 0;
    }

    public boolean hasTopDrawable() {
        return topDrawable != 0;
    }
}
