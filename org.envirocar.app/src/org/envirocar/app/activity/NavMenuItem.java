/**
 * Copyright (C) 2013 - 2015 the enviroCar community
 *
 * This file is part of the enviroCar app.
 *
 * The enviroCar app is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The enviroCar app is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with the enviroCar app. If not, see http://www.gnu.org/licenses/.
 */
package org.envirocar.app.activity;

public class NavMenuItem {

	private int id;
	private boolean enabled;
	private String title;
	private String subtitle;
	private int iconRes;

	public NavMenuItem(int id, String title, int iconRes){
		this(id,title,"",iconRes,true);
	}

	public NavMenuItem(int id, String title, String subtitle, int icon) {
		this.id = id;
		this.title = title;
		this.subtitle = subtitle;
		this.iconRes = icon;
	}

	public NavMenuItem(int id,String title, String subtitle, int iconRes, boolean enabled){
		this(id,title,subtitle,iconRes);
		setEnabled(enabled);
	}

	public int getIconRes(){
		return iconRes;
	}

	public void setIconRes(int iconRes){
		this.iconRes = iconRes;
	}

	public int getId(){
		return id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}



}
